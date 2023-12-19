package com.github.engine;

import lombok.Getter;

// GameBoard class represents the chess field
// SetWhite & SetBlack contain sets of bitboards
// for each of the 6 piece types.
// Default constructor loads standard piece constellation
// of pieces on a chess board
public abstract class GameBoard {
    @Getter
    private long[] setWhite;
    @Getter
    private long[] setBlack;

    public void loadPieceScenario(long[] setWhite, long[] setBlack) {
        this.setWhite = setWhite;
        this.setBlack = setBlack;
    }

    // Merges whitePieces and blackPieces respectively
    // decision on what is player and enemy is based on playerColor
    // returns [playerPiecesMerged, enemyPiecesMerged]
    public static long[] mergeAllPlayerBoards(int playerColor, long[] whitePieces, long[] blackPieces) {
        long mergedWhite = (whitePieces[0] | whitePieces[1] | whitePieces[2] | whitePieces[3] | whitePieces[4] | whitePieces[5]);
        long mergedBlack = (blackPieces[0] | blackPieces[1] | blackPieces[2] | blackPieces[3] | blackPieces[4] | blackPieces[5]);

        if (playerColor == 0) {
            return new long[]{mergedWhite, mergedBlack};
        }
        return new long[]{mergedBlack, mergedWhite};
    }

    public long[] mergePlayerBoardsWithExclusion(int playerColor, int playerPieceExclusion) {
        long mergedWhite = 0;
        long mergedBlack = 0;
        for (int i = 0; i < 6; i++) {
            if (playerColor == 0) {
                if (playerPieceExclusion != i) {
                    mergedWhite |= setWhite[i];
                }
                mergedBlack |= setBlack[i];
            } else {
                if (playerPieceExclusion != i) {
                    mergedBlack |= setBlack[i];
                }
                mergedWhite |= setWhite[i];
            }
        }
        return playerColor == 0 ?  new long[]{mergedWhite, mergedBlack} : new long[]{mergedBlack, mergedWhite};
    }

    public void print(int color) {
        char[] pieceSymbols = {'P', 'N', 'B', 'R', 'Q', 'K'}; // Pawn, kNight, Bishop, Rook, Queen, King
        boolean isWhite = color == 0;

        int rowStart = isWhite ? 7 : 0;
        int rowEnd = isWhite ? -1 : 8;
        int rowStep = isWhite ? -1 : 1;
        int colStart = isWhite ? 0 : 7;
        int colEnd = isWhite ? 8 : -1;
        int colStep = isWhite ? 1 : -1;

        for (int row = rowStart; isWhite ? row > rowEnd : row < rowEnd; row += rowStep) {
            System.out.println("  +---+---+---+---+---+---+---+---+");
            System.out.print((row + 1) + " |");

            for (int col = colStart; isWhite ? col < colEnd : col > colEnd; col += colStep) {
                int position = row * 8 + col;

                char symbol = ' ';
                boolean pieceFound = false;

                for (int i = 0; i < 6; i++) {
                    if (((1L << position) & this.getSetWhite()[i]) != 0) {
                        symbol = Character.toUpperCase(pieceSymbols[i]);
                        pieceFound = true;
                        break;
                    }
                }

                if (!pieceFound) {
                    for (int i = 0; i < 6; i++) {
                        if (((1L << position) & this.getSetBlack()[i]) != 0) {
                            symbol = Character.toLowerCase(pieceSymbols[i]);
                            break;
                        }
                    }
                }

                System.out.print(" " + symbol + " |");
            }

            System.out.println();
        }

        System.out.println("  +---+---+---+---+---+---+---+---+");
        if (isWhite) {
            System.out.println("    a   b   c   d   e   f   g   h");
        } else {
            System.out.println("    h   g   f   e   d   c   b   a");
        }
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
        loadPieceScenario(setWhite, setBlack);
    }

}
