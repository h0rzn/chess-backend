package com.github.engine.generator;

import com.github.engine.Bitboard;
import com.github.engine.interfaces.IBoard;
import com.github.engine.interfaces.IGenerator;
import com.github.engine.move.Move;
import com.github.engine.move.MoveType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class QueenMoveGenerator implements IBoard, IGenerator {

    private final long[] boardWhite;
    private final long[] boardBlack;

    @Getter
    private MoveType moveType;

    public QueenMoveGenerator(Bitboard board) {
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

        int queenIndex = move.getFrom().getIndex();
        // Cursor checkings current position
        long cursor = 1L << queenIndex;
        long northCursor = cursor << 8;
        long southCursor = cursor >> 8;
        long eastCursor = cursor << 1;
        long westCursor = cursor >> 1;
        long northEastCursor = cursor << 9;
        long northWestCursor = cursor << 7;
        long southEastCursor = cursor >> 7;
        long southWestCursor = cursor >> 9;
        // Max amount of positions to check for each direction
        int maxSouth = queenIndex / 8;
        int maxNorth = 8 - maxSouth - 1;
        int maxWest = queenIndex % 8;
        int maxEast = 8 - maxWest - 1;
        int maxNorthEast = Math.min(maxNorth, maxEast);
        int maxNorthWest = Math.min(maxNorth, maxWest);
        int maxSouthEast = Math.min(maxSouth, maxEast);
        int maxSouthWest = Math.min(maxSouth, maxWest);

        for (int i = 0; i < 8; i++) {
            // NORTH
            if (i < maxNorth) {
                int idx = queenIndex+ (i+1)*8;
                if ((northCursor & enemyPieces) != 0) {
                    moves.add(idx);
                    maxNorth = i - 1;
                } else if ((northCursor & ownPieces) != 0) {
                    maxNorth = i - 1;
                } else {
                    moves.add(idx);
                    northCursor <<= 8;
                }
            }

            // SOUTH
            if (i < maxSouth) {
                int idx = queenIndex- (i+1)*8;
                if ((southCursor & enemyPieces) != 0) {
                    moves.add(idx);
                    maxSouth = i - 1;
                } else if ((southCursor & ownPieces) != 0) {
                    maxSouth = i - 1;
                } else {
                    moves.add(idx);
                    southCursor >>= 8;
                }
            }

            // EAST
            if (i < maxEast) {
                int idx = queenIndex+ (i+1);
                if ((eastCursor & enemyPieces) != 0) {
                    moves.add(idx);
                    maxEast = i - 1;
                } else if ((southCursor & ownPieces) != 0) {
                    maxEast = i - 1;
                } else {
                    moves.add(idx);
                    eastCursor <<= 1;
                }
            }

            // WEST
            if (i < maxWest) {
                int idx = queenIndex- (i+1);
                if ((westCursor & enemyPieces) != 0) {
                    moves.add(idx);
                    maxWest = i - 1;
                } else if ((southCursor & ownPieces) != 0) {
                    maxWest = i - 1;
                } else {
                    moves.add(idx);
                    westCursor >>= 1;
                }
            }

            // NORTH EAST
            if (i < maxNorthEast) {
                int idx = queenIndex + (i+1)*9;
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
                int idx = queenIndex + (i+1)*7;
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
                int idx = queenIndex - (i+1)*7;
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
                int idx = queenIndex - (i+1)*7;
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
