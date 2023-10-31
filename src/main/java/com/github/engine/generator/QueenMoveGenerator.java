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
        long occupiedSquares = boardWhitePieces | boardBlackPieces;

        int queenIndex = move.getFrom().getIndex();
        long cursor = 1L << queenIndex;
        long northCursor = cursor << 8;
        long southCursor = cursor >> 8;
        long eastCursor = cursor << 1;
        long westCursor = cursor >> 1;
        long northEastCursor = cursor << 9;
        long northWestCursor = cursor << 7;
        long southEastCursor = cursor >> 7;
        long southWestCursor = cursor >> 9;

        int maxSouth = queenIndex / 8;
        int maxNorth = 8 - maxSouth - 1;
        int maxWest = queenIndex % 8;
        int maxEast = 8 - maxWest - 1;
        int maxNorthEast = Math.min(maxNorth, maxEast);
        int maxNorthWest = Math.min(maxNorth, maxWest);
        int maxSouthEast = Math.min(maxSouth, maxEast);
        int maxSouthWest = Math.min(maxSouth, maxWest);

        for (int i = 0; i < 8; i++) {
            if (i < maxNorth) {
                if ((northCursor & occupiedSquares) != 0) {
                    maxNorth = i - 1;
                } else {
                    northCursor <<= 8;
                }
            }
            if (i < maxSouth) {
                if ((southCursor & occupiedSquares) != 0) {
                    maxSouth = i - 1;
                } else {
                    northCursor <<= 8;
                }
            }
            if (i < maxEast) {
                if ((eastCursor & occupiedSquares) != 0) {
                    maxEast = i - 1;
                } else {
                    eastCursor >>= 8;
                }
            }
            if (i < maxWest) {
                if ((westCursor & occupiedSquares) != 0) {
                    maxWest = i - 1;
                } else {
                    westCursor >>= 8;
                }
            }

            if (i < maxNorthEast) {
                if ((northEastCursor & occupiedSquares) != 0) {
                    maxNorthEast = i - 1;
                } else {
                    northEastCursor >>= 8;
                }
            }
            if (i < maxNorthWest) {
                if ((northWestCursor & occupiedSquares) != 0) {
                    maxNorthWest = i - 1;
                } else {
                    northWestCursor <<= 7;
                }
            }
            if (i < maxSouthEast) {
                if ((southEastCursor & occupiedSquares) != 0) {
                    maxSouthEast = i - 1;
                } else {
                    southEastCursor >>= 7;
                }
            }
            if (i < maxSouthWest) {
                if ((southWestCursor & occupiedSquares) != 0) {
                    southWestCursor = i - 1;
                } else {
                    southWestCursor >>= 9;
                }
            }
        }

        return moves;
    }

}
