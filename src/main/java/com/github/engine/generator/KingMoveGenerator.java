package com.github.engine.generator;

import com.github.engine.Bitboard;
import com.github.engine.GameBoard;
import com.github.engine.interfaces.IBoard;
import com.github.engine.interfaces.IGenerator;
import com.github.engine.move.Move;
import com.github.engine.move.Position;

import java.util.ArrayList;
import java.util.List;

import static com.github.engine.Bitboard.mergePlayerBoards;

public class KingMoveGenerator implements IGenerator, IBoard {

    private final long[] boardWhite;
    private final long[] boardBlack;

    public KingMoveGenerator(GameBoard gameBoard) {
        this.boardWhite = gameBoard.getSetWhite();
        this.boardBlack = gameBoard.getSetBlack();
    }

    // King: Move Generation
    // fixed index offsets
    // wrapping cut of with NO_* masks
    @Override
    public long generate(int color, Position position) {
        long[] mergedBoards = GameBoard.mergePlayerBoards(color, boardWhite, boardBlack);
        long ownPieces = mergedBoards[0];
        long currentMoves = 0;

        long pos = 1L << position.getIndex();
        // north axis
        currentMoves |= (pos << 8) | (pos >> 8);
        // east
        currentMoves |= (pos << 1) & NOT_A_FILE;
        // west
        currentMoves |= (pos >> 1) & NOT_H_FILE;
        // east dia
        currentMoves |= ((pos << 9) | (pos >> 7)) & NOT_A_FILE;
        // west dia
        currentMoves |= ((pos << 7) | (pos >> 9) & NOT_H_FILE);

        return currentMoves &~ ownPieces;
    }
}
