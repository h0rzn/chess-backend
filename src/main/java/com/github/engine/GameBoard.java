package com.github.engine;

import lombok.Getter;

public abstract class GameBoard {
    @Getter
    private long[] setWhite;
    @Getter
    private long[] setBlack;

    public static long[] mergePlayerBoards(int playerColor, long[] whitePieces, long[] blackPieces) {
        long[] mergedBoards = new long[2];
        long setWhitePieces = (whitePieces[0] | whitePieces[1] | whitePieces[2] | whitePieces[3] | whitePieces[4] | whitePieces[5]);
        long setBlackPieces = (blackPieces[0] | blackPieces[1] | blackPieces[2] | blackPieces[3] | blackPieces[4] | blackPieces[5]);
        mergedBoards[0] = (playerColor == 0) ? setWhitePieces : setBlackPieces;
        mergedBoards[1] = (playerColor == 0) ? setBlackPieces : setWhitePieces;

        return mergedBoards;
    }



    public GameBoard() {
        this.setWhite = new long[6];
        this.setBlack = new long[6];

        // generate starting positions
        // PAWNS 0
        long pawnsW = 0b11111111 << 8;
        this.setWhite[0] = this.setWhite[0] | pawnsW;
        this.setBlack[0] = this.setBlack[0] | pawnsW << 40;

        // KNIGHTS 1
        long knightsW = 0b01000010;
        this.setWhite[1] = this.setWhite[1] | knightsW;
        this.setBlack[1] = this.setBlack[1] | knightsW << 56;

        // BISHOPS 2
        long bishopsW = 0b00100100;
        this.setWhite[2] = this.setWhite[2] | bishopsW;
        this.setBlack[2] = this.setBlack[2] | bishopsW << 56;

        // ROOKS 3
        long rooksW = 0b10000001;
        this.setWhite[3] = this.setWhite[3] | rooksW;
        this.setBlack[3] = this.setBlack[3] | rooksW << 56;

        // QUEENS 4 0b0001000
        long queensW = 0b0001000;
        this.setWhite[4] = this.setWhite[4] | queensW;
        this.setBlack[4] = this.setBlack[4] | (queensW << 56);

        // KINGS 5
        long kingsW = 0b00010000;
        this.setWhite[5] = this.setWhite[5] | kingsW;
        this.setBlack[5] = this.setBlack[5] | (kingsW << 56);
    }

    public GameBoard(long[] setWhite, long[] setBlack) {
        this.setWhite = setWhite;
        this.setBlack = setBlack;
    }

}
