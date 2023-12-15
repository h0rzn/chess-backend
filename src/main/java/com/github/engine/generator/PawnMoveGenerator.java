package com.github.engine.generator;

import com.github.engine.Bitboard;
import com.github.engine.GameBoard;
import com.github.engine.interfaces.IGenerator;
import com.github.engine.move.Position;

public class PawnMoveGenerator implements IGenerator {
    private final long[] boardWhite;
    private final long[] boardBlack;

    public PawnMoveGenerator(GameBoard gameBoard) {
        this.boardWhite = gameBoard.getSetWhite();
        this.boardBlack = gameBoard.getSetBlack();
    }

    // Pawn: Move Generation
    // natural move direction (north/south) depends on piece color
    // special behaviour: attack only on forward-left-dia and forward-right-dia,
    // double square move on default position (only once -> should be handled by move method)
    // not implement: en passant; promotion handled by move method
    @Override
    public long generate(int color, Position position) {
        long[] mergedBoards = GameBoard.mergePlayerBoards(color, boardWhite, boardBlack);
        long ownPieces = mergedBoards[0];
        long enemyPieces = mergedBoards[1];

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
            attacks = (pos << 9) & Bitboard.NOT_A_FILE;
            attacks |= (pos << 7) & Bitboard.NOT_H_FILE;
        } else {
            forward = (pos >> 8);
            if ((forward & enemyPieces) == 0) {
                currentMoves |= forward;
            }

            if (position.getIndex() >= 48 && position.getIndex() <= 55) {
                currentMoves |= (pos >> 16);
            }
            attacks = (pos >> 9) & Bitboard.NOT_H_FILE;
            attacks |= (pos >> 7) & Bitboard.NOT_A_FILE;
        }

        currentMoves |= attacks & enemyPieces;
        currentMoves &= ~ownPieces;
        return currentMoves;
    }
}
