package com.github.engine.generator;

import com.github.engine.GameBoard;
import com.github.engine.interfaces.IGenerator;
import com.github.engine.move.Position;


public class BishopMoveGenerator implements IGenerator {
    private final long[] boardWhite;
    private final long[] boardBlack;
    public BishopMoveGenerator(GameBoard gameBoard) {
        this.boardWhite = gameBoard.getSetWhite();
        this.boardBlack = gameBoard.getSetBlack();
    }

    // Bishop: Move Generation
    // Walks all lanes of queens star pattern
    // base logic for 'Queen Move Generation'
    @Override
    public long generate(int color, Position position) {
        long[] mergedPieces = GameBoard.mergePlayerBoards(color, boardWhite, boardWhite);
        long ownPieces = mergedPieces[0];
        long enemyPieces = mergedPieces[1];

        long currentMoves = 0;

        // Cursor checkings current position
        int index = position.getIndex();
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
                int idx = index - (i+1)*7;
                if ((southWestCursor & enemyPieces) != 0) {
                    currentMoves |= southEastCursor;
                    maxSouthWest = i;
                } else if ((southWestCursor & ownPieces) != 0) {
                    maxSouthWest = i;
                } else {
                    currentMoves |= southEastCursor;
                    maxSouthWest >>= 9;
                }
            }
        }

        return currentMoves;
    }
}
