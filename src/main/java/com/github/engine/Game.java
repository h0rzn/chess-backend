package com.github.engine;

import com.github.engine.interfaces.IBoard;
import com.github.engine.interfaces.IGame;
import com.github.engine.move.Move;
import com.github.engine.move.Position;

import java.util.List;

import static com.github.engine.move.MoveType.Promotion;

public class Game extends Bitboard implements IGame {

    public Game(){
        super();
    }

    public Game copy(){
        Game copy = new Game();
        copy.boardWhite = this.boardWhite.clone();
        copy.boardBlack = this.boardBlack.clone();
        copy.colorToMove = this.colorToMove;
        return copy;
    }

    @Override
    public List<Integer> getMoves() {
        return null;
    }

    @Override
    public boolean isCheck(int color) {
        return false;
    }

    @Override
    public boolean isCheckMate(int color) {
        return false;
    }

    @Override
    public boolean makeMove(Move move){
        int from = move.getFrom().getIndex();
        int to = move.getTo().getIndex();

        int color = this.getColorToMove();
        int pieceType = Get(from, color);

        long fromMask = 1L << from;
        long toMask = 1L << to;

        // Remove piece from old position
        if (color == 0){
            getBoardWhite()[pieceType] &= ~fromMask;
        } else {
            getBoardBlack()[pieceType] &= ~fromMask;
        }

        // Remove captured piece from new position if there is one
        for (int i = 0; i < 6; i++){
            if ((toMask & (color == 0 ? getBoardBlack()[i] : getBoardWhite()[i])) != 0){
                if (color == 0){
                    getBoardBlack()[i] &= ~toMask;
                } else {
                    getBoardWhite()[i] &= ~toMask;
                }
                break;
            }
        }

        // Add piece to new position
        if (color == 0){
            getBoardWhite()[pieceType] |= toMask;
        } else {
            getBoardBlack()[pieceType] |= toMask;
        }

        // TODO: En passant
        // TODO: Castling
        // TODO: Promotion

        return true;
    }

    // Base logic for now
    // TODO handle specific move types (Castling, Promotion, ...)
    private void syncMove(Move move) {
        int activeColor = getColorToMove();
        Position from = move.getFrom();
        Position to = move.getTo();

        long[] playerBoards;
        long[] enemyBoards;
        if (activeColor == 0) {
            playerBoards = boardWhite;
            enemyBoards = boardBlack;
        } else {
            playerBoards = boardBlack;
            enemyBoards = boardWhite;
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
        colorToMove = colorToMove == 0 ? 1 : 0;

    }
}
