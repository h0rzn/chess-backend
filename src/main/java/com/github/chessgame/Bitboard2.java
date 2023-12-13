package com.github.chessgame;


public class Bitboard2 {
    private int board;

    public int getBoard() {
        return board;
    }

    public boolean Set(int value) {
        if ((board&value) == 0) {
            this.board = this.board | value;
            return true;
        }
        return false;
    }

    public int Get(int offset) {
        int mask = 1 << offset;
        return this.board & mask;
    }

    public int Get(byte mask) {
        return this.board & (1 << mask);
    }

    public void print() {
        String board = Helpers.outAs64String(this.board);
        System.out.println(board);
    }

    public Bitboard2() {
        this.board = 0;
    }
}