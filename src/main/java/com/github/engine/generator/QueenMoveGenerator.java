package com.github.engine.generator;

import com.github.engine.Bitboard;
import com.github.engine.interfaces.IBoard;
import com.github.engine.interfaces.IGenerator;
import com.github.engine.move.Move;
import com.github.engine.move.MoveType;
import com.github.engine.move.Position;
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

    // DELETE me when interface has been adjusted to move generation
    // returning bitboard instead of ArrayList
    @Deprecated
    public List<Integer> OLD_generate(int color, Position position) {
        return new ArrayList<>();
    }

    // Queen: Move Generation
    // Walks all lanes of queens star pattern + straight lanes
    // includes reachable empty squares and reachable enemy occupied squares in legal moves bitboard
    // cursor boards mark the current inspected square of that lane
    // max values indicate squares that can possibly be iterated until board border and will be degraded to
    // the first own piece occurrence iteration index
    @Override
    public long generate(int color, Position position) {
        long[] mergedPieces = mergePlayerBoards(color, boardWhite, boardWhite);
        long ownPieces = mergedPieces[0];
        long enemyPieces = mergedPieces[1];
        long currentMoves = 0;

        int queenIndex = position.getIndex();
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
                if ((northCursor & enemyPieces) != 0) {
                    currentMoves |= northCursor;
                    maxNorth = i;
                } else if ((northCursor & ownPieces) != 0) {
                    maxNorth = i;
                } else {
                    currentMoves |= northCursor;
                    northCursor <<= 8;
                }
            }

            // SOUTH
            if (i < maxSouth) {
                if ((southCursor & enemyPieces) != 0) {
                    currentMoves |= southCursor;
                    maxSouth = i;
                } else if ((southCursor & ownPieces) != 0) {
                    maxSouth = i;
                } else {
                    currentMoves |= southCursor;
                    southCursor >>= 8;
                }
            }

            // EAST
            if (i < maxEast) {
                if ((eastCursor & enemyPieces) != 0) {
                    currentMoves |= eastCursor;
                    maxEast = i;
                } else if ((southCursor & ownPieces) != 0) {
                    maxEast = i;
                } else {
                    currentMoves |= eastCursor;
                    eastCursor <<= 1;
                }
            }

            // WEST
            if (i < maxWest) {
                if ((westCursor & enemyPieces) != 0) {
                    currentMoves |= westCursor;
                    maxWest = i;
                } else if ((southCursor & ownPieces) != 0) {
                    maxWest = i;
                } else {
                    currentMoves |= westCursor;
                    westCursor >>= 1;
                }
            }

            // NORTH EAST
            if (i < maxNorthEast) {
                if ((northEastCursor & enemyPieces) != 0) {
                    currentMoves |= northEastCursor;
                    maxNorthEast = i;
                } else if ((northEastCursor & ownPieces) != 0) {
                    maxNorthEast = i;
                } else {
                    currentMoves |= northEastCursor;
                    northEastCursor <<= 9;
                }
            }

            // NORTH WEST
            if (i < maxNorthWest) {
                if ((northWestCursor & enemyPieces) != 0) {
                    currentMoves |= northWestCursor;
                    maxNorthWest = i;
                } else if ((northWestCursor & ownPieces) != 0) {
                    maxNorthWest = i;
                } else {
                    currentMoves |= northWestCursor;
                    maxNorthWest <<= 7;
                }
            }

            // SOUTH EAST
            if (i < maxSouthEast) {
                if ((southEastCursor & enemyPieces) != 0) {
                    currentMoves |= southEastCursor;
                    maxSouthEast = i;
                } else if ((southEastCursor & ownPieces) != 0) {
                    maxSouthEast = i;
                } else {
                    currentMoves |= southEastCursor;
                    maxSouthEast >>= 7;
                }
            }

            // SOUTH WEST
            if (i < maxSouthWest) {
                if ((southWestCursor & enemyPieces) != 0) {
                    currentMoves |= southWestCursor;
                    maxSouthWest = i;
                } else if ((southWestCursor & ownPieces) != 0) {
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
