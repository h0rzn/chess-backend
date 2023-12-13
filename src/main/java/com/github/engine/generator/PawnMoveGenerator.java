package com.github.engine.generator;

import com.github.engine.Bitboard;
import com.github.engine.interfaces.IBoard;
import com.github.engine.interfaces.IGenerator;
import com.github.engine.move.Move;
import com.github.engine.move.MoveType;
import com.github.engine.move.Position;
import lombok.Getter;

import javax.sql.ConnectionPoolDataSource;
import java.util.ArrayList;
import java.util.List;

public class PawnMoveGenerator implements IBoard, IGenerator {
    private final long[] boardWhite;
    private final long[] boardBlack;

    @Getter
    private MoveType moveType;

    public PawnMoveGenerator(Bitboard board) {
        this.boardWhite = board.getBoardWhite();
        this.boardBlack = board.getBoardBlack();
    }

    public long[] precalculate() {
        long[] moves = new long[64];

        for (int i = 0; i < 64; i++) {
            long position = 1L << i;
            long currentBoard = 1L << i;

            long north = position << 8;
            currentBoard |= north;
            if (i >= 8 && i <= 15) {
                currentBoard |= (position << 16);
            }

            long northEast = (position << 8) & NOT_A_FILE;
            long northWest = (position << 7) & NOT_H_FILE;

            currentBoard |= north | northEast | northWest;

            currentBoard ^= 1L << i;
            moves[i] = currentBoard;
        }

        return moves;
    }

    // Color and T2 is passed (T2 contains move-from and move-to)
    @Override
    public List<Integer> generate(int color, Position position){
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

        // TODO: Vorzeitiges Abbrechen falls Position und Target schon übereinstimmen (Performance)
        // Single Pawn Move
        if(color == 0){
            // Creates a mask with an 1 at the target-position of the pawn
            long singleMask = 1L << position.getIndex() + 8;
            // Checks if the target-position is empty, if yes -> valid move
            if ((singleMask & emptySquares) != 0) {
                // Adds the target-position to the list of valid moves
                moves.add(position.getIndex() + 8);
                moveType = MoveType.Normal;
            }
        } else {
            // Same for other color, but with -8 instead of +8
            long singleMask = 1L << position.getIndex() - 8;
            if ((singleMask & emptySquares) != 0) {
                moves.add(position.getIndex() - 8);
                moveType = MoveType.Normal;
            }
        }

        // Double Pawn Move
        if (color == 0){
            // Same as above, but with double row move
            long doubleMask = 1L << position.getIndex() + 16;
            if ((doubleMask & emptySquares) != 0 && position.getRow() == 1) {
                moves.add(position.getIndex() + 16);
                moveType = MoveType.Normal;
            }
        } else {
            long doubleMask = 1L << position.getIndex() - 16;
            if ((doubleMask & emptySquares) != 0 && position.getRow() == 6) {
                moves.add(position.getIndex() - 16);
                moveType = MoveType.Normal;
            }
        }

        // Pawn Capture
        int pawnPosition = position.getIndex();
        long leftCaptureMask, rightCaptureMask;

        boolean notOnLeftEdge = pawnPosition % 8 != 0;
        boolean notOnRightEdge = (pawnPosition + 1) % 8 != 0;

        switch (color){
            case 0 -> {
                leftCaptureMask = 1L << pawnPosition + 7;
                if((leftCaptureMask & enemyPieces) != 0 && notOnLeftEdge){
                    moves.add(pawnPosition + 7);
                    moveType = MoveType.Capture;
                }
                rightCaptureMask = 1L << pawnPosition + 9;
                if((rightCaptureMask & enemyPieces) != 0 && notOnRightEdge){
                    moves.add(pawnPosition + 9);
                    moveType = MoveType.Capture;
                }
            }
            case 1 -> {
                leftCaptureMask = 1L << pawnPosition - 7;
                if((leftCaptureMask & enemyPieces) != 0 && notOnLeftEdge){
                    moves.add(pawnPosition - 7);
                    moveType = MoveType.Capture;
                }
                rightCaptureMask = 1L << pawnPosition - 9;
                if((rightCaptureMask & enemyPieces) != 0 && notOnRightEdge){
                    moves.add(pawnPosition - 9);
                    moveType = MoveType.Capture;
                }
            }
        }


        // En Passant

        // Promotion

        return moves;
        //

    }

    // Pawn: Move Generation
    // natural move direction (north/south) depends on piece color
    // special behaviour: attack only on forward-left-dia and forward-right-dia,
    // double square move on default position (only once -> should be handled by move method)
    // not implement: en passant; promotion handled by move method
    public long NEW_generate(int color, Position position) {
        long boardWhitePieces = (boardWhite[0] | boardWhite[1] | boardWhite[2] | boardWhite[3] | boardWhite[4] | boardWhite[5]);
        long boardBlackPieces = (boardBlack[0] | boardBlack[1] | boardBlack[2] | boardBlack[3] | boardBlack[4] | boardBlack[5]);
        long ownPieces = (color == 0) ? boardWhitePieces : boardBlackPieces;
        long enemyPieces = (color == 0) ? boardBlackPieces : boardWhitePieces;

        long pos = 1L << position.getIndex();
        long attacks;
        long forward;
        long currentMoves = 0;
        if (color == 0) {
            forward = (pos << 8);
            if ((forward & enemyPieces) == 0) {
                currentMoves |= forward;
            }

            if (position.getIndex() >= 8 && position.getIndex() <= 15) {
                currentMoves |= (pos << 16);
            }
            attacks = (pos << 9) & NOT_A_FILE;
            attacks |= (pos << 7) & NOT_H_FILE;
        } else {
            forward = (pos >> 8);
            if ((forward & enemyPieces) == 0) {
                currentMoves |= forward;
            }

            if (position.getIndex() >= 48 && position.getIndex() <= 55) {
                currentMoves |= (pos >> 16);
            }
            attacks = (pos >> 9) & NOT_H_FILE;
            attacks |= (pos >> 7) & NOT_A_FILE;
        }

        currentMoves |= attacks & enemyPieces;
        currentMoves &= ~ownPieces;
        return currentMoves;
    }
}
