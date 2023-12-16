package com.github.engine.generator;

import com.github.engine.Bitboard;
import com.github.engine.GameBoard;
import com.github.engine.interfaces.IGenerator;
import com.github.engine.move.Position;

public class KnightMoveGenerator implements IGenerator {
    private final long[] boardWhite;
    private final long[] boardBlack;

    public KnightMoveGenerator(GameBoard gameBoard) {
        this.boardWhite = gameBoard.getSetWhite();
        this.boardBlack = gameBoard.getSetBlack();
    }

    // Knight: Move Generation
    // potential squares are hardcoded and filtered by file masks
    @Override
    public long generate(int color, Position position) {
        long[] mergedBoards = GameBoard.mergePlayerBoards(color, boardWhite, boardBlack);
        long ownPieces = mergedBoards[0];

        long pos = 1L << position.getIndex();
        long spots = (pos >> 17) & Bitboard.NOT_H_FILE; // Springe 2 hoch, 1 rechts
        spots |= (pos >> 15) & Bitboard.NOT_A_FILE; // Springe 2 hoch, 1 links
        spots |= (pos >> 10) & Bitboard.NOT_GH_FILE; // Springe 1 hoch, 2 rechts
        spots |= (pos >> 6) & Bitboard.NOT_AB_FILE; // Springe 1 hoch, 2 links
        spots |= (pos << 17) & Bitboard.NOT_A_FILE; // Springe 2 runter, 1 rechts
        spots |= (pos << 15) & Bitboard.NOT_H_FILE; // Springe 2 runter, 1 links
        spots |= (pos << 10) & Bitboard.NOT_AB_FILE; // Springe 1 runter, 2 rechts
        spots |= (pos << 6) & Bitboard.NOT_GH_FILE;
        spots &= ~ownPieces;

        return spots;
    }
}
