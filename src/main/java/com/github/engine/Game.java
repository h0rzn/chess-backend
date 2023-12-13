package com.github.engine;

import com.github.engine.interfaces.IBoard;
import com.github.engine.interfaces.IGame;
import com.github.engine.move.Move;
import com.github.engine.move.Position;

import java.util.List;

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
    private void syncMove(Move move, int piece) {
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

        //
        // Placeholders
        // should come from ...somewhere?
        //
        int moveType = 0; // as Param
        int fromPiece = 0; // maybe Position.getPiece()
        int toPiece = 0;

        // Remove Player Piece
        playerBoards[piece] &= ~(1L << from.getIndex());

        // Maybe Position should also include PieceType

        switch (moveType) {
            case 0: // NORMAL
                // Add Player Piece to Destination
                playerBoards[piece] |= (1L << to.getIndex());
                // Remove Enemy Piece on Destination (noop if not needed)
                enemyBoards[toPiece] &= ~(1L << to.getIndex());
                break;
            case 1: // CASTLING
                // Remove Player Piece on Castling Destination
                playerBoards[piece] &= ~(1L << to.getIndex());
                // place as castled
                playerBoards[fromPiece] |= (1L << to.getIndex());
                playerBoards[toPiece] |= (1L << from.getIndex());
                break;
            default:

        }

        // Skip activeColor change when Promotion
        if (moveType == 3) {
            return;
        }

        // TODO Update bit on moved-indication bitboard

        // reassign of bitboards needed?
        colorToMove = colorToMove == 0 ? 1 : 0;

    }
}
