package com.github.engine;

import com.github.GameState;
import com.github.engine.generator.CheckValidator;
import com.github.engine.interfaces.IGame;
import com.github.engine.interfaces.IUserAction;
import com.github.engine.models.CheckInfo;
import com.github.engine.models.CheckResolveInfo;
import com.github.engine.models.MoveInfo;
import com.github.engine.move.Move;
import com.github.engine.move.Position;
import lombok.Getter;

import static com.github.engine.move.MoveType.Normal;
import static com.github.engine.move.MoveType.Promotion;

// Game is the high level class to be interacted with.
// Use 'execute' to send moves wrapped into a 'IUserAction'
// to trigger the move.
// For now the different move methods are still exposed,
// but this can change when the CheckValidator class is fully
// implemented.
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

    // execute a user action by calling
    // the corresponding move method depending
    // on the move type
    public MoveInfo execute(IUserAction action) {
        switch (action.getType()) {
            case Normal:
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
    // TODO add proper insertion of moveType (especially for KickOut-Type)
    public MoveInfo moveNormal(Move move) {
        MoveInfo info = new MoveInfo();

        int playerColor = getActiveColor();
        info.setPlayerColor(playerColor);

        // Abort if Promotion as awaited
        if (gameState == GameState.PROMOTION_BLACK || gameState == GameState.PROMOTION_WHITE) {
            info.setFailMessage("cannot make normal move: currently in promotion mode");
            return info;
        }

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
            }

            mergedPlayerPieces |= playerPieces[i];
            mergedEnemyPieces |= enemyPieces[i];
        }

        // to square is empty -> set piece type to type of
        // piece making the move so sync works with the same board
        // and not -1
        if (to.getPieceType() == -1) {
            to.setPieceType(from.getPieceType());
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

        // get legal moves for selected piece
        // Generator generator = new Generator(this);
        /// List<Integer> legalSquares = generator.generate(from, getColorToMove());
        // at the moment the move generator still works with square indexes
        // the new move generation returns a bitboard with legal moves marked
        // TODO adapt move generation logic to bitboard instead of indexes

        // TODO check if to is generated legal moves

        // TODO Checkmate Player
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

        // TODO handle piece Specials


        // TODO sync move
        // just force NORMAL Type for now
        move.setMoveType(Normal);
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
