package com.github.engine.generator;

import com.github.engine.GameBoard;
import com.github.engine.interfaces.IGenerator;
import com.github.engine.move.Position;

public class BishopMoveGenerator implements IGenerator {
    private final long mergedPlayerPieces;
    private final long mergedEnemyPieces;
    public BishopMoveGenerator(int playerColor, GameBoard gameBoard) {
        long[] mergedPieces = gameBoard.mergePlayerBoardsWithExclusion(playerColor, 3);
        this.mergedPlayerPieces = mergedPieces[0];
        this.mergedEnemyPieces = mergedPieces[1];
    }

    // Bishop: Move Generation
    // Walks all lanes of queens star pattern
    // base logic for 'Queen Move Generation'
    @Override
    public long generate(Position position) {
        long currentMoves = 0;

        // Cursor checkings current position
        int index = position.getIndex();
        long cursor = 1L << index;
        long northEastCursor = cursor << 9;
        long northWestCursor = cursor << 7;
        long southEastCursor = cursor >>> 7;
        long southWestCursor = cursor >>> 9;
        // Max amount of positions to check for each direction
        int maxSouth = index / 8;
        int maxNorth = 8 - maxSouth - 1;
        int maxWest = index % 8;
        int maxEast = 8 - maxWest - 1;
        int maxNorthEast = Math.min(maxNorth, maxEast);
        int maxNorthWest = Math.min(maxNorth, maxWest);
        int maxSouthEast = Math.min(maxSouth, maxEast);
        int maxSouthWest = Math.min(maxSouth, maxWest);
        //System.out.println(index + " bisphop max south west "+maxSouthWest+ " south east "+maxSouthEast);

        for (int i = 0; i < 8; i++) {
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
                    northWestCursor <<= 7;
                }
            }
            // SOUTH EAST
            if (i < maxSouthEast) {
                if ((southEastCursor & mergedEnemyPieces) != 0) {
                    //System.out.println("** bishop[se] enemy "+i);
                    currentMoves |= southEastCursor;
                    maxSouthEast = i;
                } else if ((southEastCursor & mergedPlayerPieces) != 0) {
                    //System.out.println("** bishop[se] player "+i);
                    maxSouthEast = i;
                } else {
                    //System.out.println("** bishop[se] empty "+i);
                    currentMoves |= southEastCursor;
                    southEastCursor >>>= 7;
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
                    southWestCursor >>>= 9;
                }
            }
        }

        //System.out.println("BISHOP moves: "+currentMoves);
        return currentMoves;
    }
}
