package com.github.engine.generator;

import com.github.engine.Bitboard;
import com.github.engine.BitboardOld;
import com.github.engine.IBoard;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class PawnMove implements IBoard {
    private final long[] boardWhite;
    private final long[] boardBlack;

    public PawnMove(Bitboard board) {
        this.boardWhite = board.getBoardWhite();
        this.boardBlack = board.getBoardBlack();
    }

    // Color and T2 is passed (T2 contains move-from and move-to)
    public List<Integer> generate(int color, T2<T3, T3> t2){
        List<Integer> moves = new ArrayList<>();

        // Get all pawns of color-in-turn
        long pawns = color == 0 ? boardWhite[0] : boardBlack[0];
        // Creates an Array emptySquares where all Squares are 1 and enemyPieces where all Squares are 0
        long emptySquares = ~0, enemyPieces = 0, ownPieces = 0;
        for (int i = 0; i < 6; i++) {
            // Sets all Squares where a piece is to 0, emptySquares is now a Bitboard with all empty Squares = 1
            emptySquares &= ~(boardWhite[i] | boardBlack[i]);
            // Sets all Squares where an enemy piece is to 1, enemyPieces is now a Bitboard with all enemy pieces = 1
            enemyPieces |= (color == 0 ? boardBlack[i] : boardWhite[i]);
            ownPieces |= (color == 0 ? boardWhite[i] : boardBlack[i]);
        }

        // Single Pawn Move
        long mask = 1L << t2.left().index() + 8;
        if ((mask & emptySquares) != 0) {
            moves.add(t2.left().index() + 8);
            return moves;
        }


        // Double Pawn Move

        // Pawn Capture

        // En Passant

        // Promotion

        return null;
        //

    }
}
