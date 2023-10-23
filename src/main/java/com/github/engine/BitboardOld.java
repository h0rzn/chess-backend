package com.github.engine;

import lombok.Getter;

import java.util.BitSet;

public class BitboardOld implements IBoard {

    public BitboardOld(){
        createBitBoards();
    }

    @Getter
    private BitSet[] whitePieces;
    @Getter
    private BitSet[] blackPieces;

    private static final int BOARD_SIZE = 64;

    public void createBitBoards() {
        whitePieces = new BitSet[6];
        blackPieces = new BitSet[6];

        for (int i = 0; i < 6; i++) {
            whitePieces[i] = new BitSet(BOARD_SIZE);
            blackPieces[i] = new BitSet(BOARD_SIZE);
        }

        //PAWNS (ID=0)
        for (int i = 8; i <= 15; i++) {
            whitePieces[0].set(i);
        }
        for (int i = 48; i <= 55; i++) {
            blackPieces[0].set(i);
        }

        //KNIGHTS (ID=1)
        whitePieces[1].set(1);
        whitePieces[1].set(6);
        blackPieces[1].set(57);
        blackPieces[1].set(62);

        //BISHOPS (ID=2)
        whitePieces[2].set(2);
        whitePieces[2].set(5);
        blackPieces[2].set(58);
        blackPieces[2].set(61);
        //ROOKS (ID=3)
        whitePieces[3].set(0);
        whitePieces[3].set(7);
        blackPieces[3].set(56);
        blackPieces[3].set(63);
        //QUEENS (ID=4)
        whitePieces[4].set(3);
        blackPieces[4].set(59);
        //KINGS (ID=5)
        whitePieces[5].set(4);
        blackPieces[5].set(60);
        System.out.println(whitePieces[0].toString());

    }

    //Get the piece on the square (PAWN = 0, KNIGHT = 1...)
    public int getPiece(int square) {
        for (int pieceType = 0; pieceType < 6; pieceType++) {
            if (whitePieces[pieceType].get(square)) {
                return pieceType;
            } else if (blackPieces[pieceType].get(square)) {
                return pieceType + 6;
            }
        }
        return -1;
    }

    //Set the piece on the square (PAWN = 0, KNIGHT = 1...)
    public void setPiece(int pieceType, int color, int squareIndex) {
        BitSet[] pieces = color == 0 ? whitePieces : blackPieces;

        pieces[pieceType].set(squareIndex);
    }

    public void printChessboard() {
        char[] pieceSymbols = {'P', 'N', 'B', 'R', 'Q', 'K'}; // Pawn, kNight, Bishop, Rook, Queen, King

        for (int row = 7; row >= 0; row--) {
            System.out.println("  +---+---+---+---+---+---+---+---+");
            System.out.print((row + 1) + " |");

            for (int col = 0; col < 8; col++) {
                int position = row * 8 + col;

                char symbol = ' ';
                boolean pieceFound = false;

                for (int i = 0; i < 6; i++) {
                    if (whitePieces[i].get(position)) {
                        symbol = Character.toUpperCase(pieceSymbols[i]);
                        pieceFound = true;
                        break;
                    }
                }

                if (!pieceFound) {
                    for (int i = 0; i < 6; i++) {
                        if (blackPieces[i].get(position)) {
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
        System.out.println("    a   b   c   d   e   f   g   h");
    }


    public static long convert(BitSet bits) {
        long value = 0L;
        for (int i = 0; i < bits.length(); ++i) {
            value += bits.get(i) ? (1L << i) : 0L;
        }
        return value;
    }

    public void initWhitePieces(){

    }
}
