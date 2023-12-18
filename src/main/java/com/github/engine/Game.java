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
import com.github.engine.move.Position;
import com.github.engine.utils.FenParser;
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

    public Game(){
        super();
    }

    // Create game with given board scenario
    public Game(long[] setWhite, long[] setBlack) {
        super(setWhite, setBlack);
    }

    // load a game with FEN input
    public void loadFEN(String fenString) throws Exception {
        FenParser parser = new FenParser();
        parser.parse(fenString);

        loadPieceScenario(parser.getSetWhite(), parser.getSetBlack());
        activeColor = parser.getActiveColor();
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
                System.out.println("NORMAL MOVE");
                return moveNormal(action.getMove());
            case Promotion:
                System.out.println("PROMOTION MOVE");
                return movePromotion(action.getMove());
        }
        return null;
    }

    // makeMove is the main interaction method of this engine
    // and expects a move where at least the indexes of from and to
    // are set. when the game is in promotion state this method is a noop
    // and continues to work if the promotion state is resolved by
    // successfully calling the promotion method.
    public MoveInfo moveNormal(Move move) {
        MoveInfo info = new MoveInfo();

        int playerColor = getActiveColor();
        info.setPlayerColor(playerColor);

        Position from = move.getFrom();
        Position to = move.getTo();
        long[] playerPieces;
        long[] enemyPieces;
        int attackerColor;

        if (getActiveColor() == 0) {
            playerPieces = getSetWhite();
            enemyPieces = getSetBlack();
            attackerColor = 1;
        } else {
            playerPieces = getSetBlack();
            enemyPieces = getSetWhite();
            attackerColor = 0;
        }
        info.pushLog(String.format("colors :: player: %d | enemy %d", playerColor, attackerColor));


        long mergedPlayerPieces = 0;
        long mergedEnemyPieces = 0;

        for (int i = 0; i < 6; i++) {
            long toBoard = 1L << to.getIndex();
            long fromBoard = 1L << from.getIndex();

            if ((playerPieces[i] & fromBoard) != 0) {
                from.setPieceType(i);
                // castling (go code)
                // if i == 3 && playerPieces[5]&uint64(1<<to) != 0 {
                //    toSquare.Piece = 5
                //}
            }

            if ((playerPieces[i] & toBoard) != 0) {
                to.setPieceType(i);
                move.setMoveType(Capture);
            }

            mergedPlayerPieces |= playerPieces[i];
            mergedEnemyPieces |= enemyPieces[i];
        }

        // to square is empty -> set piece type to type of
        // piece making the move so sync works with the same board
        // and not -1
        if (to.getPieceType() == -1) {
            to.setPieceType(from.getPieceType());
            move.setMoveType(Normal);
        }

        info.pushLog("POSITION from: T" + from.getPieceType() + " @" + from.getIndex());
        info.pushLog("POSITION to: T" + to.getPieceType() + " @" + to.getIndex());

        // the selected piece could not be found -> illegal move
        if (from.noPiece()) {
            info.setFailMessage("failed to find selection piece on square " + from.getIndex());
            info.setLegal(false);
            return info;
        }

        // cannot kick out enemy king
        if (((1L << from.getIndex()) & enemyPieces[5]) != 0) {
            info.setFailMessage("cannot kick out enemy king: this could be an engine error");
            info.setLegal(false);
            return info;
        }

        // Checkmate: Player
        CheckValidator playerCheckValidator = new CheckValidator(this);
        CheckInfo playerCheckInfo = playerCheckValidator.inCheck(getActiveColor());
        System.out.println(playerCheckInfo);

        long moveToBoard = (1L << to.getIndex());
        if (playerCheckInfo.isCheck()) {
            info.pushLog("player in check");
            // check if the king move is legal based on
            // given king escapes
            if (from.getPieceType() == 5) {
                // king escapes does not contain wanted move --> illegal in check move
                if ((moveToBoard&playerCheckInfo.kingEscapes()) == 0) {
                    info.setFailMessage("illegal in check move for king");
                    return info;
                }
            } else {
                // resolve by player pieces
                CheckResolveInfo resolveInfo = playerCheckValidator.isCheckResolvable(getActiveColor(), playerCheckInfo.attackBoards());
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
                            info.pushLog("legal resolve move[b2d]");;
                            moveResolvesCheck = true;
                            break;
                        }
                    }
                    // move does not resolve check --> illegal
                    if (!moveResolvesCheck) {
                        info.setFailMessage("illegal in check move");
                        return info;
                    }
                }
            }
        } else if (from.getPieceType() == 5){
            // even though the players king is currently not in check
            // we have to validate that this move would not put him in a check situation
            if ((moveToBoard& playerCheckInfo.enemyMoveCovered()) != 0) {
                info.setFailMessage("illegal non-check move: move puts king in check");
                return info;
            }
        }

        // handle piece specials
        switch (move.getFrom().getPieceType()) {
            case 0: // PROMOTION
                if ((playerColor == 0 && to.getIndex() >= 56)) {
                    move.setMoveType(Promotion);
                    gameState = GameState.PROMOTION_WHITE;
                    info.pushLog("legal promotion: next move should set promote piece");
                } else if ((playerColor == 1 && to.getIndex() <= 7)) {
                    move.setMoveType(Promotion);
                    gameState = GameState.PROMOTION_BLACK;
                    info.pushLog("legal promotion: next move should set promote piece");
                }
                break;
            case 3: // CASTLE from Rook
                if (move.getTo().getPieceType() == 5) {
                    if (isCastleLegal(move.getFrom())) {
                        move.setMoveType(Castle);
                    } else {
                        info.setFailMessage("illegal castle");
                        return info;
                    }
                }
                break;
        }

        // sync move with gameBoard
        syncMove(move);

        //
        // POST MOVE
        // check if legal move ends the game by placing enemy in checkmate
        //
        CheckValidator enemyCheckValidator = new CheckValidator(this);
        int enemyColor = 1;
        if (playerColor == 1) {
            enemyColor = 0;
        }
        CheckInfo enemyCheckInfo = enemyCheckValidator.inCheck(enemyColor);
        if (enemyCheckInfo.isCheck()) {
            CheckResolveInfo resolveInfo = enemyCheckValidator.isCheckResolvable(enemyColor, enemyCheckInfo.attackBoards());
            if (!resolveInfo.resolvable()) {
                info.pushLog("-- game ends: enemy in checkmate --");
            }
        }


        info.pushLog("++ move is legal and synced ++");
        return info;
    }

    // check if potential castle is legal
    public boolean isCastleLegal(Position from) {
        long castleRange = switch (from.getIndex()) {
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
    public MoveInfo movePromotion(Move move) {
        System.out.println("inside move promotion");
        MoveInfo info = new MoveInfo();
        info.setMove(move);
        return info;
    }

    // syncMove takes a move and syncs it with the game instance
    // even though distinguish between different move types,
    // this method does not implement game logic and expects
    // the given move to be legal
    private void syncMove(Move move) {
        int activeColor = getActiveColor();
        Position from = move.getFrom();
        Position to = move.getTo();

        long[] playerBoards;
        long[] enemyBoards;
        if (activeColor == 0) {
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
        // because we wait for Promotion call before switchting sides
        if (move.getMoveType() == Promotion) {
            return;
        }

        // TODO Update bit on moved-indication bitboard

        // reassign of bitboards needed?
        // Update color
        activeColor = activeColor == 0 ? 1 : 0;

    }

}
