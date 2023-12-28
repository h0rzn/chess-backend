package com.github.engine.generator;

import com.github.engine.Bitboard;
import com.github.engine.GameBoard;
import com.github.engine.interfaces.IGenerator;
import com.github.engine.move.Position;


public class KingMoveGenerator implements IGenerator {
    private final long mergedPlayerPieces;

    public KingMoveGenerator(int playerColor, GameBoard gameBoard) {
        long[] mergedPieces = gameBoard.mergePlayerBoardsWithExclusion(playerColor, 3);
        this.mergedPlayerPieces = mergedPieces[0];
    }

    // King: Move Generation
    // fixed index offsets
    // wrapping cut of with NO_* masks
    @Override
    public long generate(Position position) {
        long currentMoves = 0;

        long pos = 1L << position.getIndex();
        // north axis
        currentMoves |= (pos << 8) | (pos >> 8);
        // east
        currentMoves |= (pos << 1) & Bitboard.NOT_A_FILE;
        // west
        currentMoves |= (pos >> 1) & Bitboard.NOT_H_FILE;
        // east dia
        currentMoves |= ((pos << 9) | (pos >> 7)) & Bitboard.NOT_A_FILE;
        // west dia
        currentMoves |= ((pos << 7) | (pos >> 9) & Bitboard.NOT_H_FILE);

        return currentMoves;
        // return currentMoves &~ mergedPlayerPieces;
    }
}
