package com.github.chessgame;

//Deprecated weil die ganzen implementierten Methoden schon von BitSet implementiert werden.
//Klasse kann entfernt werden.
@Deprecated()
public class Bitboard {
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

    public Bitboard() {
        this.board = 0;
    }
}
