package com.github.engine.generator;

import com.github.engine.GameBoard;
import com.github.engine.interfaces.IGenerator;
import com.github.engine.move.Position;

public class RookMoveGenerator implements IGenerator {
    private final long[] boardWhite;
    private final long[] boardBlack;
    public RookMoveGenerator(GameBoard gameBoard) {
        this.boardWhite = gameBoard.getSetWhite();
        this.boardBlack = gameBoard.getSetBlack();
    }

    // Rook: Move Generation
    // works similar to queen, but extended with castling check
    @Override
    public long generate(int color, Position position) {
        long[] mergedPieces = GameBoard.mergePlayerBoards(color, boardWhite, boardWhite);
        long ownPieces = mergedPieces[0];
        long enemyPieces = mergedPieces[1];
        long currentMoves = 0;
    
        int rookIndex = position.getIndex();
        // Cursor checkings current position
        long cursor = 1L << rookIndex;
        long northCursor = cursor << 8;
        long southCursor = cursor >> 8;
        long eastCursor = cursor << 1;
        long westCursor = cursor >> 1;
        // Max amount of positions to check for each direction
        int maxSouth = rookIndex / 8;
        int maxNorth = 8 - maxSouth - 1;
        int maxWest = rookIndex % 8;
        int maxEast = 8 - maxWest - 1;
        
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
        }

        // bitboard with current moves is 0
        // if 0: all pieces have been moved -> ignore castling
        if (currentMoves == 0) {
            return currentMoves;
        }

        // we don't care which side of the board the king is on
        long kingPosPotentials = (1 << 4) | (1L << 60);
        // no match of potential king position and own pieces
        if ((kingPosPotentials&ownPieces) == 0) {
            return currentMoves;
        }
        // this should only catch our king
        if ((currentMoves&kingPosPotentials) == 0) {
            return currentMoves;
        }
        // just check if the rook has been moved and we are done
        long rookBoard = (1L << rookIndex);
        if ((rookBoard&currentMoves) != 0) {
            currentMoves |= 0x10;
        }
        
        return currentMoves;
    }
}
