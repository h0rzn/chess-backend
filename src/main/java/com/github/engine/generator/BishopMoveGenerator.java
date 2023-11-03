package com.github.engine.generator;

import com.github.engine.Bitboard;
import com.github.engine.interfaces.IBoard;
import com.github.engine.interfaces.IGenerator;
import com.github.engine.move.Move;

import java.util.ArrayList;
import java.util.List;

public class BishopMoveGenerator implements IBoard, IGenerator {
    private final long[] boardWhite;
    private final long[] boardBlack;
    public BishopMoveGenerator(Bitboard board) {
        this.boardWhite = board.getBoardWhite();
        this.boardBlack = board.getBoardBlack();
    }

    public long[] precalculate() {
        long hFile = 0x8080808080808080L;
        long aFile = 0x101010101010101L;
        long diaTTR = 0x8040201008040201L;
        long diaTTL = 0x102040810204080L;

        long[] moves = new long[64];

        for (int i = 0; i < 64; i++) {
            long currentBoard = 1L << i;
            int column = i % 8;


            // B Left -> T Right
            long ttrKeep = 0L;
            for (int n = 0; n < (7-column); n++) {
                ttrKeep |= (hFile >> n);
            }
            currentBoard |= (diaTTR&ttrKeep);

            // T Right -> B Left
            long diaTBR = diaTTL >> (63-i);
            currentBoard |= (diaTBR&ttrKeep);

            // B Right -> T Left
            long ttlKeep = 0L;
            for (int n = 0; n < column; n++) {
                ttlKeep |= (aFile << n);
            }
            currentBoard |= ((diaTTL << i) &ttlKeep);

            // T Right -> B Left
            long diaTBL = diaTTL >> (63-i);
            currentBoard |= (diaTBL&ttlKeep);

            currentBoard ^= 1L << i;
            moves[i] = currentBoard;
        }

        return moves;
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
        long northEastCursor = cursor << 9;
        long northWestCursor = cursor << 7;
        long southEastCursor = cursor >> 7;
        long southWestCursor = cursor >> 9;
        // Max amount of positions to check for each direction
        int maxSouth = index / 8;
        int maxNorth = 8 - maxSouth - 1;
        int maxWest = index % 8;
        int maxEast = 8 - maxWest - 1;
        int maxNorthEast = Math.min(maxNorth, maxEast);
        int maxNorthWest = Math.min(maxNorth, maxWest);
        int maxSouthEast = Math.min(maxSouth, maxEast);
        int maxSouthWest = Math.min(maxSouth, maxWest);

        for (int i = 0; i < 8; i++) {
            // NORTH EAST
            if (i < maxNorthEast) {
                int idx = index + (i+1)*9;
                if ((northEastCursor & enemyPieces) != 0) {
                    moves.add(idx);
                    maxNorthEast = i - 1;
                } else if ((northEastCursor & ownPieces) != 0) {
                    maxNorthEast = i - 1;
                } else {
                    moves.add(idx);
                    northEastCursor <<= 9;
                }
            }
            // NORTH WEST
            if (i < maxNorthWest) {
                int idx = index + (i+1)*7;
                if ((northWestCursor & enemyPieces) != 0) {
                    moves.add(idx);
                    maxNorthWest = i - 1;
                } else if ((northWestCursor & ownPieces) != 0) {
                    maxNorthWest = i - 1;
                } else {
                    moves.add(idx);
                    maxNorthWest <<= 7;
                }
            }
            // SOUTH EAST
            if (i < maxSouthEast) {
                int idx = index - (i+1)*7;
                if ((southEastCursor & enemyPieces) != 0) {
                    moves.add(idx);
                    maxSouthEast = i - 1;
                } else if ((southEastCursor & ownPieces) != 0) {
                    maxSouthEast = i - 1;
                } else {
                    moves.add(idx);
                    maxSouthEast >>= 7;
                }
            }
            // SOUTH WEST
            if (i < maxSouthWest) {
                int idx = index - (i+1)*7;
                if ((southWestCursor & enemyPieces) != 0) {
                    moves.add(idx);
                    maxSouthWest = i - 1;
                } else if ((southWestCursor & ownPieces) != 0) {
                    maxSouthWest = i - 1;
                } else {
                    moves.add(idx);
                    maxSouthWest >>= 9;
                }
            }
        }

        return moves;
    }
}
