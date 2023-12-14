package com.github.engine.generator;

import com.github.engine.Bitboard;
import com.github.engine.interfaces.IBoard;
import com.github.engine.interfaces.IGenerator;
import com.github.engine.move.Move;
import com.github.engine.move.Position;

import java.util.ArrayList;
import java.util.List;

import static com.github.engine.Bitboard.mergePlayerBoards;

public class KnightMoveGenerator implements IGenerator, IBoard {
    private final long[] boardWhite;
    private final long[] boardBlack;

    public KnightMoveGenerator(Bitboard board) {
        this.boardWhite = board.getBoardWhite();
        this.boardBlack = board.getBoardBlack();
    }

    @Deprecated
    public List<Integer> OLD_generate(int color, Position position) {
        List<Integer> moves = new ArrayList<>();

        // Gets the knight bitboard
        long knights = color == 0 ? boardWhite[1] : boardBlack[1];
        // Gets own pieces
        long ownPieces = 0;
        for (int i = 0; i < 6; i++) {
            ownPieces |= (color == 0 ? boardWhite[i] : boardBlack[i]);
        }

        // Gets the knight position
        int knightPosition = position.getIndex();

        //Creates a bitboard with the knight on the knightPosition
        long knightMask = 1L << knightPosition;

        // Creates a bitboard with all possible moves
        long spots = ((long) knightMask >> 17) & NOT_H_FILE; // Springe 2 hoch, 1 rechts
        spots |= (knightMask >> 15) & NOT_A_FILE; // Springe 2 hoch, 1 links
        spots |= (knightMask >> 10) & NOT_GH_FILE; // Springe 1 hoch, 2 rechts
        spots |= (knightMask >> 6) & NOT_AB_FILE; // Springe 1 hoch, 2 links
        spots |= (knightMask << 17) & NOT_A_FILE; // Springe 2 runter, 1 rechts
        spots |= (knightMask << 15) & NOT_H_FILE; // Springe 2 runter, 1 links
        spots |= (knightMask << 10) & NOT_AB_FILE; // Springe 1 runter, 2 rechts
        spots |= (knightMask << 6) & NOT_GH_FILE;
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

    // Knight: Move Generation
    // potential squares are hardcoded and filtered by file masks
    @Override
    public long generate(int color, Position position) {
        long[] mergedBoards = Bitboard.mergePlayerBoards(color, boardWhite, boardBlack);
        long ownPieces = mergedBoards[0];
        long enemyPieces = mergedBoards[1];

        long pos = 1L << position.getIndex();
        long spots = (pos >> 17) & NOT_H_FILE; // Springe 2 hoch, 1 rechts
        spots |= (pos >> 15) & NOT_A_FILE; // Springe 2 hoch, 1 links
        spots |= (pos >> 10) & NOT_GH_FILE; // Springe 1 hoch, 2 rechts
        spots |= (pos >> 6) & NOT_AB_FILE; // Springe 1 hoch, 2 links
        spots |= (pos << 17) & NOT_A_FILE; // Springe 2 runter, 1 rechts
        spots |= (pos << 15) & NOT_H_FILE; // Springe 2 runter, 1 links
        spots |= (pos << 10) & NOT_AB_FILE; // Springe 1 runter, 2 rechts
        spots |= (pos << 6) & NOT_GH_FILE;
        spots &= ~ownPieces;

        return spots;
    }
}
