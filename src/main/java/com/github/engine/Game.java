package com.github.engine;

import com.github.GameState;
import com.github.engine.generator.CheckValidator;
import com.github.engine.generator.Generator;
import com.github.engine.interfaces.IGame;
import com.github.engine.interfaces.IUserAction;
import com.github.engine.models.CheckInfo;
import com.github.engine.models.CheckResolveInfo;
import com.github.engine.models.MoveInfo;
import com.github.engine.move.Move;
import com.github.engine.move.MoveType;
import com.github.engine.move.Position;
import com.github.engine.utils.FenParser;
import com.github.engine.utils.FenSerializer;
import lombok.Getter;

import java.util.Arrays;

import static com.github.engine.move.MoveType.*;

// Game is the high level class to be interacted with.
// Use 'execute' to send moves wrapped into a 'IUserAction'
// to trigger the move.
// For now the different move methods are still exposed,
// but this can change when the CheckValidator class is fully
// implemented.
//
// HOW TO MAKE A MOVE
// As an action:
//    1) create a MoveAction
//       - normal move: use MoveAction(move)
//       - promotion  : use MoveAction(pieceTypeToPromoteTo)
//    2) pass to: execute(moveAction)
//       -> returns MoveInfo
// or directly (not recommended as some validity checks are not made):
// Normal Move: moveNormal(move)
// Promotion  : movePromotion(move) --> not fully implemented
public class Game extends GameBoard implements IGame {
    @Getter
    private GameState gameState;
    @Getter
    private int activeColor;
    @Getter
    private String lastMoveFen;

    public Game(){
        super();
        FenSerializer serializer = new FenSerializer(this);
        this.lastMoveFen = serializer.serializeAll();
    }

    // Create game with given board scenario
    public Game(long[] setWhite, long[] setBlack) {
        super(setWhite, setBlack);
        FenSerializer serializer = new FenSerializer(this);
        this.lastMoveFen = serializer.serializeAll();
    }

    public Game(String fen) throws Exception {
        load(fen);
    }

    // initialize game with default settings (default piece positions, ...)
    public void load() {
        loadDefault();
    }

    // load game with fen string
    public void load(String fen) throws Exception {
        // reset game before we load fen
        reset();

        FenParser parser = new FenParser();
        parser.parse(fen);

        loadPieceScenario(parser.getSetWhite(), parser.getSetBlack());
        activeColor = parser.getActiveColor();
        lastMoveFen = new FenSerializer(this).serializeAll();
    }


    // load a game with FEN input
    public void loadFEN(String fenString) throws Exception {
        // reset game before we load fen
        reset();

        FenParser parser = new FenParser();
        parser.parse(fenString);

        loadPieceScenario(parser.getSetWhite(), parser.getSetBlack());
        activeColor = parser.getActiveColor();
        lastMoveFen = new FenSerializer(this).serializeAll();
    }

    // fully reset game
    public void reset() {
        gameState = GameState.UNKOWN;
        // reset color to 0 (white); updated color should be set by loadFen
        activeColor = 0;
        lastMoveFen = "";
        // clear boards from GameBoard
        clearBoards();
    }

    // execute a user action by calling
    // the corresponding move method depending
    // on the move type
    public MoveInfo execute(IUserAction action) {
        switch (action.getType()) {
            case Normal:
                // Abort if Promotion as awaited
                if (gameState == GameState.PROMOTION_BLACK || gameState == GameState.PROMOTION_WHITE) {
                    MoveInfo info = new MoveInfo();
                    info.setFailMessage("cannot make normal move: currently in promotion mode");
                    return info;
                }
                return moveNormal(action.getMove());
            case Promotion:
                return movePromotion(action);
            default:
                MoveInfo info = new MoveInfo();
                info.setLegal(false);
                info.setFailMessage("unkown action type: "+action.getType());
                return info;
        }
    }

    // makeMove is the main interaction method of this engine
    // and expects a move where at least the indexes of from and to
    // are set. when the game is in promotion state this method is a noop
    // and continues to work if the promotion state is resolved by
    // successfully calling the promotion method.
    public MoveInfo moveNormal(Move move) {
        MoveInfo info = new MoveInfo();
        info.setPlayerColor(getActiveColor());

        Position from = move.getFrom();
        Position to = move.getTo();
        long[] enemyPieces;
        int attackerColor;

        if (getActiveColor() == 0) {
            enemyPieces = getSetBlack();
            attackerColor = 1;
        } else {
            enemyPieces = getSetWhite();
            attackerColor = 0;
        }
        info.pushLog(String.format("colors :: player: %d | enemy %d", activeColor, attackerColor));

        // extend move
        move = extendMove(move);

        // the selected piece could not be found -> illegal move
        if (from.noPiece()) {
            return info.WithFailure("failed to find selection piece on square " + from.getIndex(), move);
        }

        // cannot kick out enemy king
        if (((1L << from.getIndex()) & enemyPieces[5]) != 0) {
            return info.WithFailure("cannot kick out enemy king: this could be an engine error", move);
        }


        // is destination square reachable
        Generator generator = new Generator(getActiveColor(), this);
        long legalMoves = generator.generate(from, false);
        long moveToBoard = (1L << to.getIndex());

        if ((legalMoves&moveToBoard) == 0) {
            return info.WithFailure("destination square is not reachable (not in move gen)", move);
        }

        // is destination square occupied by enemy? --> CAPTURE
        if (move.getTo().getColor() != getActiveColor()) {
            move.setMoveType(Capture);
        }

        // Checkmate: Player
        CheckValidator playerCheckValidator = new CheckValidator(this);
        CheckInfo playerCheckInfo = playerCheckValidator.inCheck(getActiveColor());

        if (playerCheckInfo.isCheck()) {
            info.pushLog("player in check: "+playerCheckInfo);
            // check if the king move is legal based on
            // given king escapes
            if (from.getPieceType() == 5) {
                // king escapes does not contain wanted move --> illegal in check move
                if ((moveToBoard&playerCheckInfo.kingEscapes()) == 0) {
                    return info.WithFailure("illegal in check move for king", move);
                }
            } else {
                // resolve by player pieces
                CheckResolveInfo resolveInfo = playerCheckValidator.isCheckResolvable(getActiveColor(), playerCheckInfo.attackBoards(), playerCheckInfo.enemyMoveCovered());
                if (resolveInfo.resolvable()) {
                    // check if move is legal attack to defend move
                    // or legal block to defend move
                    boolean moveResolvesCheck = false;
                    for (int playerPiece = 0; playerPiece < 6; playerPiece++) {
                        long[] a2d = resolveInfo.attack2Defend();
                        long[] b2d = resolveInfo.block2Defend();

                        if ((a2d[playerPiece]&moveToBoard) != 0) {
                            info.pushLog("legal resolve move[a2d]");
                            moveResolvesCheck = true;
                            break;
                        } else if ((b2d[playerPiece]&moveToBoard) != 0) {
                            info.pushLog("legal resolve move[b2d]");
                            moveResolvesCheck = true;
                            break;
                        }
                    }
                    // move does not resolve check --> illegal
                    if (!moveResolvesCheck) {
                        return info.WithFailure("illegal in check move", move);
                    }
                }
            }
        } else if (from.getPieceType() == 5){
            // even though the players king is currently not in check
            // we have to validate that this move would not put him in a check situation
            if ((moveToBoard& playerCheckInfo.enemyMoveCovered()) != 0) {
                return info.WithFailure("illegal non-check move: move puts king in check", move);
            }
        }

        // handle specials
        switch (move.getMoveType()) {
            case Promotion:
                gameState = activeColor == 0 ? GameState.PROMOTION_WHITE : GameState.PROMOTION_BLACK;
                info.pushLog("legal promotion: next move should set promote piece");
                break;
            case Castle:
                if (isCastleLegal(move)) {
                    info.pushLog("legal castle");
                } else {
                    return info.WithFailure("illegal castle", move);
                }
                break;
            case PawnDouble:
                System.out.println("PAWN DOUBLE!!!");
                if (getActiveColor() == 0 && !isDoublePawnedWhite()) {
                    setDoublePawnedWhite(true);
                } else if (getActiveColor() == 1 && !isDoublePawnedBlack()) {
                    setDoublePawnedBlack(true);
                } else {
                    return info.WithFailure("no double pawn move left", move);
                }
        }

        // sync move with gameBoard
        info.setLegal(true);
        syncMove(move);

        //
        // POST MOVE
        // check if legal move ends the game by placing enemy in checkmate
        // syncMoves flipped colors!
        // ignore post move verification on promotion, because has not finished move
        if (move.getMoveType() != Promotion) {
            System.out.println("=== POST MOVE ===");
            CheckValidator enemyCheckValidator = new CheckValidator(this);
            CheckInfo enemyCheckInfo = enemyCheckValidator.inCheck(getActiveColor());
            System.out.println("enemy check info for ["+getActiveColor()+"] "+
                    "attackBoards "+ Arrays.toString(enemyCheckInfo.attackBoards()) +
                    " eCovers " + enemyCheckInfo.enemyMoveCovered() +
                    " kingEscapes " + enemyCheckInfo.kingEscapes()
            );
            if (enemyCheckInfo.isCheck()) {
                info.pushLog("move puts enemy in check");
                CheckResolveInfo resolveInfo = enemyCheckValidator.isCheckResolvable(getActiveColor(), enemyCheckInfo.attackBoards(), enemyCheckInfo.enemyMoveCovered());
                if (!resolveInfo.resolvable()) {
                    info.pushLog("-- game ends: enemy in checkmate --");
                }
                System.out.println("enemy resolve info: "+resolveInfo.resolvable()+
                        " a2d's "+Arrays.toString(resolveInfo.attack2Defend())+
                        " b2d's "+Arrays.toString(resolveInfo.block2Defend()));
            }
            System.out.println("=== POST END  ===");
        }



        System.out.println("\n--- Returning Move ---");
        System.out.println("--- OLD FEN "+lastMoveFen);

        String fen = FenSerializer.serializeGameboard(this);
        info.setStateFEN(fen);
        info.setCaptures(getCaptures());
        lastMoveFen = fen;
        info.pushLog("++ move is legal and synced ++");
        info.setMove(move);
        System.out.println("--- NEW FEN "+lastMoveFen);
        return info;
    }

    // extend move
    // 1) fill pieceTypes (if toPiece -1 -> fromPiece type)
    // 2) castle or promotion move type
    public Move extendMove(Move move) {
        long[] playerPieces;
        long [] enemyPieces;
        int enemyColor;
        if (getActiveColor() == 0) {
            playerPieces = getSetWhite();
            enemyPieces = getSetBlack();
            enemyColor = 1;
        } else {
            playerPieces = getSetBlack();
            enemyPieces = getSetWhite();
            enemyColor = 0;
        }

        for (int i = 0; i < 6; i++) {
            long toBoard = 1L << move.getTo().getIndex();
            long fromBoard = 1L << move.getFrom().getIndex();

            // player
            if ((playerPieces[i] & fromBoard) != 0) {
                move.getFrom().setPieceType(i);
                move.getTo().setColor(getActiveColor());
            }
            if ((playerPieces[i] & toBoard) != 0) {
                move.getTo().setPieceType(i);
                move.getTo().setColor(getActiveColor());
            }
            // destination could be enemy square
            if ((enemyPieces[i] & toBoard) != 0) {
                move.getTo().setPieceType(i);
                move.getTo().setColor(enemyColor);
            }
        }

        System.out.println("DOUBLE? " + Math.abs(move.getTo().getIndex()-move.getFrom().getIndex()));

        // to square is empty -> set piece type to type of
        // piece making the move so sync works with the same board
        // and not -1
        if (move.getTo().noPiece()) {
            move.getTo().setPieceType(move.getFrom().getPieceType());
        }

        // PawnDouble
        if (Math.abs(move.getTo().getIndex()-move.getFrom().getIndex()) == 16) {
            move.setMoveType(PawnDouble);
            return move;
        }

        // Promotion
        if (move.getFrom().getPieceType() == 0) {
            if ((activeColor == 0 && move.getTo().getIndex() >= 56)) {
                move.setMoveType(Promotion);
            } else if ((activeColor == 1 &&  move.getTo().getIndex() <= 7)) {
                move.setMoveType(Promotion);
            }
            return move;
        }

        // Castling
        if (move.getFrom().getPieceType() == 3 && move.getTo().getPieceType() == 5) {
            move.setMoveType(Castle);
            return move;
        }


        move.setMoveType(Normal);
        return move;
    }

    // check if potential castle is legal
    public boolean isCastleLegal(Move move) {
        long castleRange = switch (move.getFrom().getIndex()) {
            case 0 -> 0xeL;
            case 7 -> 0x60L;
            case 56 -> 0xe00000000000000L;
            case 63 -> 0x6000000000000000L;
            default -> 0;
        };

        if (castleRange == 0) {
            return false;
        }

        long[] playerPieces;
        Generator enemyGenerator;
        if (activeColor == 0) {
            playerPieces = getSetWhite();
            enemyGenerator = new Generator(1, this);
        } else {
            playerPieces = getSetBlack();
            enemyGenerator = new Generator(0, this);
        }

        long[] enemyMoves = enemyGenerator.generateAll();
        for (int i = 0; i < 6; i++) {
            // check if there are player pieces in castling range
            if ((playerPieces[i] & castleRange) != 0) {
                return false;
            }
            // check if enemy piece attack castling range
            if ((enemyMoves[i] & castleRange) != 0) {
                return false;
            }
        }
        return true;
    }

    // promote a piece if game is in promotion mode
    public MoveInfo movePromotion(IUserAction action) {
        MoveInfo info = new MoveInfo();
        Move move = action.getMove();
        // check if we can promote
        if (!(gameState == GameState.PROMOTION_WHITE || gameState == GameState.PROMOTION_BLACK)) {
            info.setFailMessage("not in promotion mode");
            return info;
        }

        int promoteTo = action.promoteTo();
        if (promoteTo > 0 && promoteTo < 5) {
            // update bitboards
            if (activeColor == 0) {
                long[] pieces = getSetWhite();
                pieces[0] &= ~(1L << move.getFrom().getIndex());

                pieces[action.promoteTo()] |= (1L << move.getFrom().getIndex());
                setSetWhite(pieces);
            } else {
                long[] pieces = getSetBlack();
                pieces[0] &= ~(1L << move.getFrom().getIndex());

                pieces[action.promoteTo()] |= (1L << move.getFrom().getIndex());
                setSetBlack(pieces);
            }
            info.pushLog("promoting "+action.promoteTo()+ " of player "+activeColor);
            return info;
        }


        info.Fail("illegal piece to promote to");
        return info;
    }

    // syncMove takes a move and syncs it with the game instance
    // even though distinguish between different move types,
    // this method does not implement game logic and expects
    // the given move to be legal
    private void syncMove(Move move) {
        Position from = move.getFrom();
        Position to = move.getTo();

        long[] playerBoards;
        long[] enemyBoards;
        if (getActiveColor() == 0) {
            playerBoards = getSetWhite();
            enemyBoards = getSetBlack();
        } else {
            playerBoards = getSetBlack();
            enemyBoards = getSetWhite();
        }

        // Remove Player Piece
        playerBoards[from.getPieceType()] &= ~(1L << from.getIndex());
        // Maybe Position should also include PieceType

        switch (move.getMoveType()) {
            case Capture:
                addCapture(move);
            case Normal:
                // Add Player Piece to Destination
                playerBoards[from.getPieceType()] |= (1L << to.getIndex());
                // Remove Enemy Piece on Destination (noop if not needed)
                enemyBoards[to.getPieceType()] &= ~(1L << to.getIndex());
                break;
            case Castle:
                // Remove Player Piece on Castling Destination
                playerBoards[to.getPieceType()] &= ~(1L << to.getIndex());
                // place as castled
                playerBoards[from.getPieceType()] |= (1L << to.getIndex());
                playerBoards[to.getPieceType()] |= (1L << from.getIndex());
                break;
            default:
                // explicitly catch 'Unkown' case?
                // should probably return early here -> maybe return false?
        }

        // Skip activeColor change when Promotion
        // because we wait for Promotion call before switching sides
        if (move.getMoveType() == Promotion) {
            return;
        }

        // update unmoved bitboard (noop if bit is already 0)
        markMovedPiece(move.getFrom().getIndex());

        // reassign of bitboards needed?
        // Update color
        this.activeColor = getActiveColor() == 0 ? 1 : 0;
    }

}
