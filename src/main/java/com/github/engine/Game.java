package com.github.engine;

import com.github.engine.check.CheckValidator;
import com.github.engine.generator.Generator;
import com.github.engine.interfaces.IGame;
import com.github.engine.interfaces.IUserAction;
import com.github.engine.models.CheckInfo;
import com.github.engine.models.CheckResolveInfo;
import com.github.engine.models.MoveInfo;
import com.github.engine.move.Move;
import com.github.engine.move.Position;
import com.github.engine.utils.FenParser;
import com.github.engine.utils.FenSerializer;
import lombok.Getter;

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
    private Move lastMove;

    public Game(){
        super();
        this.lastMoveFen = FenSerializer.serialize(this);
    }

    // Create game with given board scenario
    public Game(long[] setWhite, long[] setBlack) {
        super(setWhite, setBlack);
        this.lastMoveFen = FenSerializer.serialize(this);
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
        this.lastMoveFen = FenSerializer.serialize(this);
    }


    // load a game with FEN input
    public void loadFEN(String fenString) throws Exception {
        // reset game before we load fen
        reset();

        FenParser parser = new FenParser();
        parser.parse(fenString);

        loadPieceScenario(parser.getSetWhite(), parser.getSetBlack());
        activeColor = parser.getActiveColor();
        this.lastMoveFen = FenSerializer.serialize(this);
    }

    // fully reset game
    public void reset() {
        gameState = GameState.DEFAULT;
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
        MoveInfo info = new MoveInfo();
        if (isGameOver()) {
            return info.WithGameOver(gameState);
        }

        switch (action.getMove().getMoveType()) {
            case Normal:
                System.out.println("\n[ENGINE] executing move: <NORMAL>");
                info = moveNormal(action.getMove());
                System.out.println("[ENGINE] move executed\n");
                break;
            case Promotion:
                System.out.println("\n[ENGINE] executing move: <PROMOTION>");
                info = movePromotion(action);
                System.out.println("[ENGINE] move executed\n");
                break;
            default:
                System.out.println("\n[ENGINE] received move of type <UNKNOWN>");
                info = new MoveInfo();
                info.setLegal(false);
                info.setFailMessage("unknown action type: "+action.getType());
                System.out.println("[ENGINE] denied unknown move\n");
                break;
        }

        return info;
    }

    private boolean isGameOver() {
        return gameState == GameState.END_WHITE_IN_CHECKMATE || gameState == GameState.END_BLACK_IN_CHECKMATE;
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
        System.out.println("+ extended move: "+move.toString());

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

        System.out.println("+ move gen: destination<"+moveToBoard+"> legals<"+legalMoves+">");

        if ((legalMoves&moveToBoard) == 0) {
            return info.WithFailure("destination square is not reachable (not in move gen)", move);
        }

        // Checkmate: Player
        CheckValidator playerCheckValidator = new CheckValidator(this);
        CheckInfo playerCheckInfo = playerCheckValidator.inCheck(getActiveColor());
        if (playerCheckInfo.isCheck()) {
            System.out.println("+ player check: "+playerCheckInfo);
            info.pushLog("enemy: in check");
            CheckResolveInfo playerResolveInfo = playerCheckValidator.isCheckResolvable(getActiveColor(), playerCheckInfo);
            if (playerResolveInfo.resolvable()) {
                // look up if move is legal resolve move
                long[] a2d = playerResolveInfo.attack2Defend();
                long[] b2d = playerResolveInfo.block2Defend();
                boolean isLegalResolveMove = false;
                for (int playerPiece = 0; playerPiece < 6; playerPiece++) {
                    if ((a2d[playerPiece]&moveToBoard) != 0) {
                        info.pushLog("check: legal a2d resolve move");
                        isLegalResolveMove = true;
                        break;
                    } else if ((b2d[playerPiece]&moveToBoard) != 0) {
                        info.pushLog("check: legal b2d resolve move");
                        isLegalResolveMove = true;
                        break;
                    }
                }

                if (!isLegalResolveMove) {
                    return info.WithFailure("illegal in check move", move);
                }

            }
        } else { // No Check: Player
            if (move.getFrom().getPieceType() == 5) {
                // king escapes does not contain wanted move --> illegal in check move
                // remove enemy covered squares from king escape squares
                long kingLegalMoves = playerCheckInfo.kingEscapes() &~ playerCheckInfo.enemyMoveCovered();
                if ((moveToBoard & kingLegalMoves) == 0) {
                    // illegal in check move for king
                    return info.WithFailure("king: illegal in check move", move);
                }
            }
        }

        // handle specials except Promotion
        switch (move.getMoveType()) {
            case Castle:
                System.out.println("+ specials: handling <CASTLE>");
                if (isCastleLegal(move)) {
                    info.pushLog("legal castle");
                } else {
                    return info.WithFailure("illegal castle", move);
                }
                break;
            case PawnDouble:
                if (getActiveColor() == 0 && !isDoublePawnedWhite()) {
                    setDoublePawnedWhite(true);
                } else if (getActiveColor() == 1 && !isDoublePawnedBlack()) {
                    setDoublePawnedBlack(true);
                } else {
                    return info.WithFailure("no double pawn move left", move);
                }
                System.out.println("+ specials: handling <DOUBLEPAWN>");
        }

        //
        // POST MOVE
        // check if legal move ends the game by placing enemy in checkmate
        // ignore post move verification on promotion, because has not finished move
        System.out.println("+ cur FEN: "+lastMoveFen);
        syncMove(move);
        System.out.println("+ new FEN: "+lastMoveFen);

        // syncMove flips active player color
        CheckInfo enemyCheckInfo = playerCheckValidator.inCheck(getActiveColor());
        if (enemyCheckInfo.isCheck()) {
            System.out.println("+ enemy check: "+enemyCheckInfo);
            info.pushLog("enemy: in check");
            CheckResolveInfo enemyResolveInfo = playerCheckValidator.isCheckResolvable(getActiveColor(), enemyCheckInfo);
            if (!enemyResolveInfo.resolvable()) {
                // update gameState
                this.gameState = (getActiveColor() == 0) ? GameState.END_WHITE_IN_CHECKMATE : GameState.END_BLACK_IN_CHECKMATE;
                info.pushLog("-- game ends: enemy in checkmate ("+this.gameState+") --");
            }
        }


        return info.WithSuccess(move, lastMoveFen, getCaptures());
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
            // destination is enemy square
            if ((enemyPieces[i] & toBoard) != 0) {
                move.getTo().setPieceType(i);
                move.getTo().setColor(enemyColor);
                move.setMoveType(Capture);
            }
        }

        if (move.getTo().noPiece()) {
            // Normal
            // to square is empty -> set piece type to type of
            // piece making the move so sync works with the same board
            // and not -1
            move.getTo().setPieceType(move.getFrom().getPieceType());
            move.setMoveType(Normal);
        } else if (move.getFrom().getPieceType() == 0) {
            // Promotion
            int fromSquare = move.getFrom().getIndex();
            int toSquare = move.getTo().getIndex();
            if (getActiveColor() == 0) {
                if ((fromSquare >= 48 && fromSquare <= 55) && toSquare >= 56) {
                    move.setMoveType(Promotion);
                    return move;
                }
            } else {
                if ((fromSquare > 48 && fromSquare <= 55) && toSquare >= 56) {
                    move.setMoveType(Promotion);
                    return move;
                }
            }
        } else if (Math.abs(move.getTo().getIndex()-move.getFrom().getIndex()) == 16) {
            // PawnDouble
            move.setMoveType(PawnDouble);
            return move;
        } else if (move.getFrom().getPieceType() == 3 && move.getTo().getPieceType() == 5) {
            // Castling
            move.setMoveType(Castle);
            return move;
        }

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

        // TODO check if move values are set

        // extend move?
        move = extendMove(move);
        if (move.getMoveType() != Promotion) {
            return info.WithFailure("extending move did not detect promotion type", move);
        }
        System.out.println("+ extended move: "+move);

        // check if square is legally reachable (move generation)
        Generator generator = new Generator(getActiveColor(), this);
        long legalMoves = generator.generate(move.getFrom(), false);
        long moveToBoard = (1L << move.getTo().getIndex());
        if ((legalMoves&moveToBoard) == 0) {
            return info.WithFailure("promotion: destination square is not reachable (not in move gen)", move);
        }
        System.out.println("+ move gen: destination<"+moveToBoard+"> legals<"+legalMoves+">");

        // check if promote to piece is legal
        int promoteTo = action.promoteTo();
        if (!(promoteTo > 0 && promoteTo < 5)) {
            return info.WithFailure("promotion: illegal promote to piece "+promoteTo, move);
        }

        // manually set toPiece to promotion piece
        move.getTo().setPieceType(promoteTo);

        // sync move with gameBoard
        System.out.println("+ cur FEN "+lastMoveFen);
        syncMove(move);
        System.out.println("+ new FEN "+lastMoveFen);

        return info.WithSuccess(move, lastMoveFen, getCaptures());
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
                // Remove Enemy Piece on Destination
                enemyBoards[to.getPieceType()] &= ~(1L << to.getIndex());
            case PawnDouble:
            case Normal:
                // Add Player Piece to Destination
                playerBoards[from.getPieceType()] |= (1L << to.getIndex());
                break;
            case Castle:
                // Remove Player Piece on Castling Destination
                playerBoards[to.getPieceType()] &= ~(1L << to.getIndex());
                // place as castled
                playerBoards[from.getPieceType()] |= (1L << to.getIndex());
                playerBoards[to.getPieceType()] |= (1L << from.getIndex());
                break;
            case Promotion:
                // remove pawn piece
                playerBoards[from.getPieceType()] &= ~(1L << from.getIndex());
                // promote piece
                playerBoards[to.getPieceType()] |= (1L << to.getIndex());
                break;
            default:
                // explicitly catch 'Unkown' case?
                // should probably return early here -> maybe return false?
        }

        // update unmoved bitboard (noop if bit is already 0)
        markMovedPiece(move.getFrom().getIndex());

        // Update color
        this.activeColor = getActiveColor() == 0 ? 1 : 0;

        this.lastMoveFen = FenSerializer.serializeUpdate(this, move);
    }

}
