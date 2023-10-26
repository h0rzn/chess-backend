package com.github.engine.generator;

import com.github.engine.Bitboard;
import com.github.engine.interfaces.IBoard;
import com.github.engine.interfaces.IGenerator;

import java.util.ArrayList;
import java.util.List;

public class KingMoveGenerator implements IGenerator, IBoard {

    private final long[] boardWhite;
    private final long[] boardBlack;

    public KingMoveGenerator(Bitboard bitboard) {
        this.boardWhite = bitboard.getBoardWhite();
        this.boardBlack = bitboard.getBoardBlack();
    }

    @Override
    public List<Integer> generate(int color, T2<T3, T3> t2) {
        List<Integer> moves = new ArrayList<>();
        long king = color == 0 ? boardWhite[5] : boardBlack[5];
        // Creates an Array emptySquares where all Squares are 1 and enemyPieces where all Squares are 0
        long emptySquares = ~0, enemyPieces = 0, ownPieces = 0;
        for (int i = 0; i < 6; i++) {
            // Sets all Squares where a piece is to 0, emptySquares is now a Bitboard with all empty Squares = 1
            emptySquares &= ~(boardWhite[i] | boardBlack[i]);
            // Sets all Squares where an enemy piece is to 1, enemyPieces is now a Bitboard with all enemy pieces = 1
            enemyPieces |= (color == 0 ? boardBlack[i] : boardWhite[i]);
            ownPieces |= (color == 0 ? boardWhite[i] : boardBlack[i]);
        }

        // Gets the position of the king
        long kingPosition = 1L << t2.left().index();
        // Creates a mask with an 1 at the forward and backward position of the king
        long spots = (kingPosition << 8) | (kingPosition >> 8);
        // Creates a mask with an 1 at the left and right position of the king
        long leftRightSpots = (kingPosition & NOT_H_FILE << 1) | (kingPosition & NOT_A_FILE >> 1);
        // Creates a mask with an 1 at the diagonal position of the king
        long diagonalSpots = (kingPosition & NOT_A_FILE << 7) | (kingPosition & NOT_H_FILE >> 7) |
                (kingPosition & NOT_H_FILE << 9) | (kingPosition & NOT_A_FILE >> 9);

        // Creates a mask with all possible moves
        spots |= leftRightSpots | diagonalSpots;
        // Removes all own pieces from the mask
        spots &= ~ownPieces;
        // Adds all possible moves to the list
        for (int j = 0; j < 64; j++) {
            if ((spots & (1L << j)) != 0) {
                moves.add(j);
            }
        }
        System.out.println(moves);


        return moves;
    }
}
