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

        long[] ownBoard = (color == 0 ? boardWhite: boardBlack);
        long ownPieces = (ownBoard[0] | ownBoard[1] | ownBoard[2] | ownBoard[3] | ownBoard[5]);

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
                northCursor <<= 8;
            }
            if (i < maxSouth) {
                southCursor >>= 8;
            }
            if (i < maxEast) {
                eastCursor >>= 8;
            }
            if (i < maxWest) {
                westCursor >>= 8;
            }

            if (i < maxNorthEast) {
                northEastCursor <<= 9;
            }
            if (i < maxNorthWest) {
                northWestCursor <<= 7;
            }
            if (i < maxSouthEast) {
                southEastCursor >>= 7;
            }
            if (i < maxSouthWest) {
                southWestCursor >>= 9;
            }
        }



        return moves;
    }

}
