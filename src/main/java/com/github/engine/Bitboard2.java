package com.github.engine;

import java.math.BigInteger;
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

    // Get all stored bitboards merged into one (returns long)
    public long GetMerged() {
        long boardAll = 0L;

        for (long board : this.boardWhite) {
            boardAll = boardAll | board;
        }
        for (long board : this.boardBlack) {
            boardAll = boardAll | board;
        }

        return boardAll;
    }

    // Just for testing around
    public void Print64() {
        long boardAll = this.GetMerged();

        String binStr = Long.toBinaryString(boardAll);

        String bin = String.format("%064d%n", new BigInteger(binStr));

        String[] split = bin.split("(?<=\\G.{" + 8 + "})");
        for (String row : split) {
            System.out.println(row);
        }
    }

    public Bitboard2() {
        this.boardWhite = new long[6];
        this.boardBlack = new long[6];

        // generate starting positions
        // PAWNS 0
        long pawnsW = 0b11111111 << 8;
        this.boardWhite[0] = this.boardWhite[0] | pawnsW;
        this.boardBlack[0] = this.boardBlack[0] | pawnsW << 40;

        // KNIGHTS 1
        long knightsW = 0b01000010;
        this.boardWhite[1] = this.boardWhite[1] | knightsW;
        this.boardBlack[1] = this.boardBlack[1] | knightsW << 56;

        // BISHOPS 2
        long bishopsW = 0b00100100;
        this.boardWhite[2] = this.boardWhite[2] | bishopsW;
        this.boardBlack[2] = this.boardBlack[2] | bishopsW << 56;

        // ROOKS 3
        long rooksW = 0b10000001;
        this.boardWhite[3] = this.boardWhite[3] | rooksW;
        this.boardBlack[3] = this.boardBlack[3] | rooksW << 56;

        // QUEENS 4
        long queensW = 0b00010000;
        this.boardWhite[4] = this.boardWhite[4] | queensW;
        this.boardBlack[4] = this.boardBlack[4] | (queensW << 56);

        // KINGS 5
        long kingsW = 0b0001000;
        this.boardBlack[5] = this.boardBlack[5] | kingsW;
        this.boardBlack[5] = this.boardBlack[5] | (kingsW << 56);
    }
}
