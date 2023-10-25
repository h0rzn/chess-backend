package com.github.engine;

import com.github.engine.interfaces.IBoard;
import com.github.engine.interfaces.IGame;

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
    public boolean makeMove(IBoard.T2<IBoard.T3, IBoard.T3> t2){
        int from = t2.left().index();
        int to = t2.right().index();

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
}
