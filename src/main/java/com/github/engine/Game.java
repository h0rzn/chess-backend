package com.github.engine;

import com.github.GameState;
import com.github.engine.interfaces.IGame;
import com.github.engine.interfaces.IUserAction;
import com.github.engine.models.MoveInfo;
import com.github.engine.move.Move;
import com.github.engine.move.Position;
import lombok.Getter;

import static com.github.engine.move.MoveType.Normal;
import static com.github.engine.move.MoveType.Promotion;

public class Game extends GameBoard implements IGame {
    @Getter
    private GameState gameState;
    @Getter
    private int activeColor;

    public Game(){
        super();
    }

    public Game copy(){
        // Game copy = new Game();
        // TOOD check if we have to copy gameboard
        return null;
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
    public MoveInfo moveNormal(Move move) {
        int playerColor = getActiveColor();

        // ---

        MoveInfo info = new MoveInfo();

        // Abort if Promotion as awaited

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

        System.out.println("from: T" + from.getPieceType() + " @" + from.getIndex());
        System.out.println("to: T" + to.getPieceType() + " @" + to.getIndex());

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

        // TODO handle piece Specials

        // TODO sync move
        // just force NORMAL Type for now
        move.setMoveType(Normal);
        syncMove(move);

        // TODO Checkmate Enemy -> game over?

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
                enemyBoards[to.getIndex()] &= ~(1L << to.getIndex());
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
