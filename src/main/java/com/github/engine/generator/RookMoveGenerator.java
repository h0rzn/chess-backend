package com.github.engine.generator;

import com.github.engine.Bitboard;
import com.github.engine.interfaces.IBoard;
import com.github.engine.interfaces.IGenerator;
import com.github.engine.move.Move;

import java.util.ArrayList;
import java.util.List;

public class RookMoveGenerator implements IBoard, IGenerator {
    private final long[] boardWhite;
    private final long[] boardBlack;
    public RookMoveGenerator(Bitboard board) {
        this.boardWhite = board.getBoardWhite();
        this.boardBlack = board.getBoardBlack();
    }

    @Override
    public List<Integer> generate(int color, Move move) {
        List<Integer> moves = new ArrayList<>();

        long boardWhitePieces = (boardWhite[0] | boardWhite[1] | boardWhite[2] | boardWhite[3] | boardWhite[4] | boardWhite[5]);
        long boardBlackPieces = (boardBlack[0] | boardBlack[1] | boardBlack[2] | boardBlack[3] | boardBlack[4] | boardBlack[5]);
        long ownPieces = (color == 0) ? boardWhitePieces : boardBlackPieces;
        long enemyPieces = (color == 0) ? boardBlackPieces : boardWhitePieces;
        // Cursor checkings current position
        int index = move.getFrom().getIndex();
        long cursor = 1L << index;
        long northCursor = cursor << 8;
        long southCursor = cursor >> 8;
        // Max amount of positions to check for each direction
        int maxSouth = index / 8;
        int maxNorth = 8 - maxSouth - 1;

        for (int i = 0; i < 8; i++) {
            // NORTH
            if (i < maxNorth) {
                int idx = index+(i+1)*8;
                if ((northCursor & enemyPieces) != 0) {
                    moves.add(idx);
                    maxNorth = i;
                } else if ((northCursor & ownPieces) != 0) {
                    maxNorth = i;
                } else {
                    moves.add(idx);
                    northCursor <<= 8;
                }
            }

            // SOUTH
            if (i < maxSouth) {
                int idx = index+(i+1)*8;
                if ((southCursor & enemyPieces) != 0) {
                    moves.add(idx);
                    maxSouth = i;
                } else if ((southCursor & ownPieces) != 0) {
                    maxSouth = i;
                } else {
                    moves.add(idx);
                    southCursor >>= 8;
                }
            }
        }

        return moves;
    }
}
