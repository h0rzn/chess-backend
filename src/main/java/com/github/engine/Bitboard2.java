package com.github.engine;

import java.util.BitSet;

public class Bitboard2 {
    private long[] boardWhite;
    private long[] boardBlack;

    // Takes field index, piece type and color index and enables the
    // respective bit. Returns true on success or false on failure.
    public boolean Set(int index, int piece, int color) {
        long positionMask = 1L << index;
        long[] colorBoards = color == 0 ? this.boardWhite : this.boardBlack;

        long pieceBoard = colorBoards[piece];
        if ((pieceBoard&positionMask) == 0) {
            colorBoards[piece] = pieceBoard | positionMask;
            return true;
        } else {
            return false;
        }
    }

    // Get piece type (0-6: white, > black) by board index.
    // Returns -1 on failure.
    public int Get(int index) {
        int pieceWhite = Get(index, 0);
        if (pieceWhite >= 0) {
            return pieceWhite;
        }

        int pieceBlack = Get(index, 0);
        if (pieceBlack >= 0) {
            return pieceBlack;
        }

        return -1;
    }

    // Get piece type by index and color
    public int Get(int index, int color) {
        long positionMask = 1L << index;
        long[] colorBoards = color == 0 ? this.boardWhite : this.boardBlack;

        for (int pieceType = 0; pieceType < 6; pieceType++) {
            if ((colorBoards[pieceType] & positionMask) != 0) {
                return pieceType;
            }
        }
        return -1;
    }

    public Bitboard2() {
        this.boardWhite = new long[6];
        this.boardBlack = new long[6];

        // generate starting positions
    }
}
