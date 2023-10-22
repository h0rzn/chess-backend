package com.github;

import com.github.engine.Bitboard;

import java.util.BitSet;

public class Main {
    public static void main(String[] args) {
       Bitboard board = new Bitboard();
        long pawns = 0x00FF000000000000L;
        long knights = 0x0000000000000042L;
        long fila = 0x0101010101010101L;

        System.out.println(longToBitSet(pawns));
        System.out.println("Pawn: " + board.getPiece(8));
        System.out.println("Nothing: " + board.getPiece(16));


    }

    public static BitSet longToBitSet(long value) {
        BitSet bitSet = new BitSet(Long.SIZE); // Long.SIZE ist 64, da ein long 64 Bits hat
        for (int i = 0; i < Long.SIZE; i++) {
            if ((value & (1L << i)) != 0) {
                bitSet.set(i);
            }
        }
        return bitSet;
    }
}