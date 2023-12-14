package com.github.engine;

import com.github.engine.interfaces.IBoard;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.math.BigInteger;

public abstract class Bitboard {
    @Getter
    public long[] boardWhite;
    @Getter
    public long[] boardBlack;
    @Getter
    public int colorToMove;

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

    public static boolean readBit(long number, int position) {
        if(position < 0 || position >= Long.SIZE) {
            throw new IllegalArgumentException("Position muss zwischen 0 und " + (Long.SIZE - 1) + " liegen");
        }

        long mask = 1L << position;
        long result = number & mask;

        return result != 0;
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

    // Creates a copy of the current bitboard, used for check resolve validation


    public void printChessboard(int color) {
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
                    if (readBit(this.boardWhite[i], position)) {
                        symbol = Character.toUpperCase(pieceSymbols[i]);
                        pieceFound = true;
                        break;
                    }
                }

                if (!pieceFound) {
                    for (int i = 0; i < 6; i++) {
                        if (readBit(this.boardBlack[i], position)) {
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

    // Merges whitePieces and blackPieces respectively
    // decision on what is player and enemy is based on playerColor
    // returns [playerPiecesMerged, enemyPiecesMerged]
    public static long[] mergePlayerBoards(int playerColor, long[] whitePieces, long[] blackPieces) {
        long[] mergedBoards = new long[2];
        long boardWhitePieces = (whitePieces[0] | whitePieces[1] | whitePieces[2] | whitePieces[3] | whitePieces[4] | whitePieces[5]);
        long boardBlackPieces = (blackPieces[0] | blackPieces[1] | blackPieces[2] | blackPieces[3] | blackPieces[4] | blackPieces[5]);
        mergedBoards[0] = (playerColor == 0) ? boardWhitePieces : boardBlackPieces;
        mergedBoards[1] = (playerColor == 0) ? boardBlackPieces : boardWhitePieces;

        return mergedBoards;
    }

    /*
    public long[] mergeStoredBoards() {
        if(getColorToMove() == 0) {
            return Bitboard.mergePlayerBoards(0, getBoardWhite(), getBoardBlack());
        }

        return new long[]{};
    }

     */

    public void turn(){
        colorToMove = colorToMove == 0 ? 1 : 0;
    }

    public Bitboard() {
        this.boardWhite = new long[6];
        this.boardBlack = new long[6];
        this.colorToMove = 0;

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

        // QUEENS 4 0b0001000
        long queensW = 0b0001000;
        this.boardWhite[4] = this.boardWhite[4] | queensW;
        this.boardBlack[4] = this.boardBlack[4] | (queensW << 56);

        // KINGS 5
        long kingsW = 0b00010000;
        this.boardWhite[5] = this.boardWhite[5] | kingsW;
        this.boardBlack[5] = this.boardBlack[5] | (kingsW << 56);
    }
}
