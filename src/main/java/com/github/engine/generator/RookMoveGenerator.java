package com.github.engine.generator;

import com.github.engine.Bitboard;
import com.github.engine.interfaces.IBoard;
import com.github.engine.interfaces.IGenerator;
import com.github.engine.move.Move;
import com.github.engine.move.Position;

import java.util.ArrayList;
import java.util.List;

public class RookMoveGenerator implements IBoard, IGenerator {
    private final long[] boardWhite;
    private final long[] boardBlack;
    public RookMoveGenerator(Bitboard board) {
        this.boardWhite = board.getBoardWhite();
        this.boardBlack = board.getBoardBlack();
    }

    @Deprecated
    public List<Integer> OLD_generate(int color, Position position) {
        List<Integer> moves = new ArrayList<>();

        long boardWhitePieces = (boardWhite[0] | boardWhite[1] | boardWhite[2] | boardWhite[3] | boardWhite[4] | boardWhite[5]);
        long boardBlackPieces = (boardBlack[0] | boardBlack[1] | boardBlack[2] | boardBlack[3] | boardBlack[4] | boardBlack[5]);
        long ownPieces = (color == 0) ? boardWhitePieces : boardBlackPieces;
        long enemyPieces = (color == 0) ? boardBlackPieces : boardWhitePieces;
        // Cursor checkings current position
        int index = position.getIndex();
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

        // CASTLING
        // Initial King position
        long kingDefaultPosition = (color == 0) ? 0b00010000L : 0b00010000L << 56;
        long kingPosition = (color == 0) ? this.boardWhite[5] : this.boardBlack[5];

        // Initial positions for rooks
        long rookOnePosition = (color == 0) ? 0b1L : 0b1L << 56;
        long rookTwoPosition = (color == 0) ? 0b10000000 : 0b10000000L << 56;

        // 1. king and rook have not been moved
        if ((kingDefaultPosition&kingPosition) != 0) {

            // TODO 2. king not in check

            long castlingRangeOne = 0b1110L;
            long castlingRangeTwo = 0b1100000L;
            if ((cursor&rookOnePosition) != 0) {
                long checkRange = (color == 0) ? castlingRangeOne : castlingRangeOne << 56;
                if ((enemyPieces&checkRange) == 0) {
                    moves.add(0);
                }


            } else if ((cursor&rookTwoPosition) != 0) {
                long checkRange = (color == 0) ? castlingRangeTwo : castlingRangeTwo << 56;
                if ((enemyPieces%checkRange) == 0) {
                    moves.add(7);
                }
            }
        }

        return moves;
    }

    @Override
    public long generate(int color, Position position) {
        long[] mergedPieces = mergePlayerBoards(color, boardWhite, boardWhite);
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
