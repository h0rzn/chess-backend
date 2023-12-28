package com.github.engine.generator;

import com.github.engine.Bitboard;
import com.github.engine.GameBoard;
import com.github.engine.interfaces.IGenerator;
import com.github.engine.move.Position;

public class PawnMoveGenerator implements IGenerator {
    private final long mergedPlayerPieces;
    private final long mergedEnemyPieces;
    private final int playerColor;

    public PawnMoveGenerator(int playerColor, GameBoard gameBoard) {
        long[] mergedPieces = gameBoard.mergePlayerBoardsWithExclusion(playerColor, 3);
        this.mergedPlayerPieces = mergedPieces[0];
        this.mergedEnemyPieces = mergedPieces[1];
        this.playerColor = playerColor;
    }

    // Pawn: Move Generation
    // natural move direction (north/south) depends on piece color
    // special behaviour: attack only on forward-left-dia and forward-right-dia,
    // double square move on default position (only once -> should be handled by move method)
    // not implement: en passant; promotion handled by move method
    // DANGER: attacks are generally included for check verification
    @Override
    public long generate(Position position) {
        long pos = 1L << position.getIndex();
        long attacks;
        long currentMoves = 0;
        long forward = (playerColor == 0) ? pos << 8 : pos >>> 8;

        if (playerColor == 0) {
            if ((forward & mergedEnemyPieces) == 0) {
                currentMoves |= forward;
            }

            if (position.getIndex() >= 8 && position.getIndex() <= 15) {
                currentMoves |= (pos << 16);
            }
            attacks = (pos << 9) & Bitboard.NOT_A_FILE;
            attacks |= (pos << 7) & Bitboard.NOT_H_FILE;
        } else {
            if ((forward & mergedEnemyPieces) == 0) {
                currentMoves |= forward;
            }

            if (position.getIndex() >= 48 && position.getIndex() <= 55) {
                currentMoves |= (pos >> 16);
            }
            attacks = (pos >>> 9) & Bitboard.NOT_H_FILE;
            attacks |= (pos >>> 7) & Bitboard.NOT_A_FILE;
        }

        // Use this instead if attacks should only be included if
        // attack square is occupied by enemy
        // currentMoves |= attacks & mergedEnemyPieces;
        currentMoves |= attacks;

        // currentMoves &= ~mergedPlayerPieces;
        return currentMoves;
    }
}
