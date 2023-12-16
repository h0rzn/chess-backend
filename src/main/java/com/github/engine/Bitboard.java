package com.github.engine;


import java.util.ArrayList;
import java.util.List;

// Utility Class for working with bitboards
public class Bitboard {
    // Bitboards for the border ranks of a chessboard, used for move generation
    // and general bitboard operations
    public static long NOT_A_FILE = 0xfefefefefefefefeL;
    public static long NOT_H_FILE = 0x7f7f7f7f7f7f7f7fL;
    public static long NOT_AB_FILE = 0xfcfcfcfcfcfcfcfcL;
    public static long NOT_GH_FILE = 0x3f3f3f3f3f3f3f3fL;

    // Pass in a bitboard and get index of the first set bit found
    public static int bitscanSingle(long board) {
        for (int i = 0; i < 64; i++) {
            if ((board&(1L<<i)) != 0) {
                return i;
            }
        }
        return -1;
    }

    // Pass in a bitboard and get indexes of all set bits
    public static List<Integer> bitscanMulti(long board) {
        List<Integer> squares = new ArrayList<>();
        for (int i = 0; i < 64; i++) {
            if ((board&(1L<<i)) != 0) {
                squares.add(i);
            }
        }
        return squares;
    }
}
