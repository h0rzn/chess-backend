package com.github.engine.generator;

import com.github.engine.GameBoard;
import com.github.engine.interfaces.IGenerator;
import com.github.engine.move.Position;

public class QueenMoveGenerator implements IGenerator {
    private final long mergedPlayerPieces;
    private final long mergedEnemyPieces;

    public QueenMoveGenerator(int playerColor, GameBoard gameBoard) {
        long[] mergedPieces = gameBoard.mergePlayerBoardsWithExclusion(playerColor, 3);
        this.mergedPlayerPieces = mergedPieces[0];
        this.mergedEnemyPieces = mergedPieces[1];
    }

    // Queen: Move Generation
    // Walks all lanes of queens star pattern + straight lanes
    // includes reachable empty squares and reachable enemy occupied squares in legal moves bitboard
    // cursor boards mark the current inspected square of that lane
    // max values indicate squares that can possibly be iterated until board border and will be degraded to
    // the first own piece occurrence iteration index
    @Override
    public long generate(Position position) {
        long currentMoves = 0;

        int queenIndex = position.getIndex();
        // Cursor checking current position
        long cursor = 1L << queenIndex;
        long northCursor = cursor << 8;
        long southCursor = cursor >>> 8;
        long eastCursor = cursor << 1;
        long westCursor = cursor >>> 1;
        long northEastCursor = cursor << 9;
        long northWestCursor = cursor << 7;
        long southEastCursor = cursor >>> 7;
        long southWestCursor = cursor >>> 9;
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
                if ((northCursor & mergedEnemyPieces) != 0) {
                    currentMoves |= northCursor;
                    maxNorth = i;
                } else if ((northCursor & mergedPlayerPieces) != 0) {
                    maxNorth = i;
                } else {
                    currentMoves |= northCursor;
                    northCursor <<= 8;
                }
            }

            // SOUTH
            if (i < maxSouth) {
                if ((southCursor & mergedEnemyPieces) != 0) {
                    currentMoves |= southCursor;
                    maxSouth = i;
                } else if ((southCursor & mergedPlayerPieces) != 0) {
                    maxSouth = i;
                } else {
                    currentMoves |= southCursor;
                    southCursor >>= 8;
                }
            }

            // EAST
            if (i < maxEast) {
                if ((eastCursor & mergedEnemyPieces) != 0) {
                    currentMoves |= eastCursor;
                    maxEast = i;
                } else if ((southCursor & mergedPlayerPieces) != 0) {
                    maxEast = i;
                } else {
                    currentMoves |= eastCursor;
                    eastCursor <<= 1;
                }
            }

            // WEST
            if (i < maxWest) {
                if ((westCursor & mergedEnemyPieces) != 0) {
                    currentMoves |= westCursor;
                    maxWest = i;
                } else if ((southCursor & mergedPlayerPieces) != 0) {
                    maxWest = i;
                } else {
                    currentMoves |= westCursor;
                    westCursor >>= 1;
                }
            }

            // NORTH EAST
            if (i < maxNorthEast) {
                if ((northEastCursor & mergedEnemyPieces) != 0) {
                    currentMoves |= northEastCursor;
                    maxNorthEast = i;
                } else if ((northEastCursor & mergedPlayerPieces) != 0) {
                    maxNorthEast = i;
                } else {
                    currentMoves |= northEastCursor;
                    northEastCursor <<= 9;
                }
            }

            // NORTH WEST
            if (i < maxNorthWest) {
                if ((northWestCursor & mergedEnemyPieces) != 0) {
                    currentMoves |= northWestCursor;
                    maxNorthWest = i;
                } else if ((northWestCursor & mergedPlayerPieces) != 0) {
                    maxNorthWest = i;
                } else {
                    currentMoves |= northWestCursor;
                    maxNorthWest <<= 7;
                }
            }

            // SOUTH EAST
            if (i < maxSouthEast) {
                if ((southEastCursor & mergedEnemyPieces) != 0) {
                    currentMoves |= southEastCursor;
                    maxSouthEast = i;
                } else if ((southEastCursor & mergedPlayerPieces) != 0) {
                    maxSouthEast = i;
                } else {
                    currentMoves |= southEastCursor;
                    maxSouthEast >>= 7;
                }
            }

            // SOUTH WEST
            if (i < maxSouthWest) {
                if ((southWestCursor & mergedEnemyPieces) != 0) {
                    currentMoves |= southWestCursor;
                    maxSouthWest = i;
                } else if ((southWestCursor & mergedPlayerPieces) != 0) {
                    maxSouthWest = i;
                } else {
                    currentMoves |= southWestCursor;
                    maxSouthWest >>= 9;
                }
            }
        }

        return currentMoves;
    }

}
