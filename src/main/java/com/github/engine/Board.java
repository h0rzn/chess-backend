package com.github.engine;

import com.github.chessgame.Bitboard;

import java.util.BitSet;

public class Board {

    public Board(){
        createBitBoards();
        printBoard();
        System.out.println(getPiece(2).name());
    }

    private BitSet[] WBitBoards = new BitSet[6];
    private BitSet[] BBitBoards = new BitSet[6];

    //White Pieces
    private BitSet W_Pawn_BB = new BitSet(64);
    private BitSet W_Rook_BB = new BitSet(64);
    private BitSet W_Knight_BB = new BitSet(64);
    private BitSet W_Bishop_BB = new BitSet(64);
    private BitSet W_Queen_BB = new BitSet(64);
    private BitSet W_King_BB = new BitSet(64);

    //Black Pieces
    private BitSet B_Pawn_BB = new BitSet(64);
    private BitSet B_Rook_BB = new BitSet(64);
    private BitSet B_Knight_BB = new BitSet(64);
    private BitSet B_Bishop_BB = new BitSet(64);
    private BitSet B_Queen_BB = new BitSet(64);
    private BitSet B_King_BB = new BitSet(64);

    String chessBoard[][] = {
        {"r","k","b","q","a","b","k","r"},
        {"p","p","p","p","p","p","p","p"},
        {" "," "," "," "," "," "," "," "},
        {" "," "," "," "," "," "," "," "},
        {" "," "," "," "," "," "," "," "},
        {" "," "," "," "," "," "," "," "},
        {"P","P","P","P","P","P","P","P"},
        {"R","K","B","Q","A","B","K","R"}
    };

    public void setPiece(){

    }

    public Piece getPiece(int square) {
        for (Piece piece : Piece.values()) {
            if (getPieceBitBoard(piece).get(square)) {
                return piece;
            }
        }

        return Piece.None;
    }

    public void printBoard() {
        BitSet board = new BitSet(64);
        board.or(W_Pawn_BB);
        board.or(W_Rook_BB);
        board.or(W_Knight_BB);
        board.or(W_Bishop_BB);
        board.or(W_Queen_BB);
        board.or(W_King_BB);
        board.or(B_Pawn_BB);
        board.or(B_Rook_BB);
        board.or(B_Knight_BB);
        board.or(B_Bishop_BB);
        board.or(B_Queen_BB);
        board.or(B_King_BB);

        for (int i = 0; i < 64; i++) {
            if (board.get(i)) {
                System.out.print("1");
            } else {
                System.out.print("0");
            }
            if ((i + 1) % 8 == 0) {
                System.out.println();
            }
        }
    }

    public BitSet getPieceBitBoard(Piece piece) {
        switch (piece) {
            case WPawn:
                return W_Pawn_BB;
            case WRook:
                return W_Rook_BB;
            case WKnight:
                return W_Knight_BB;
            case WBishop:
                return W_Bishop_BB;
            case WQueen:
                return W_Queen_BB;
            case WKing:
                return W_King_BB;
            case BPawn:
                return B_Pawn_BB;
            case BRook:
                return B_Rook_BB;
            case BKnight:
                return B_Knight_BB;
            case BBishop:
                return B_Bishop_BB;
            case BQueen:
                return B_Queen_BB;
            case BKing:
                return B_King_BB;
            default:
                throw new IllegalArgumentException("Ungültiger Spielstein: " + piece);
        }
    }


    public void createBitBoards() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                switch (chessBoard[i][j]) {
                    case "P":
                        W_Pawn_BB.set(i * 8 + j);
                        break;
                    case "R":
                        W_Rook_BB.set(i * 8 + j);
                        break;
                    case "K":
                        W_Knight_BB.set(i * 8 + j);
                        break;
                    case "B":
                        W_Bishop_BB.set(i * 8 + j);
                        break;
                    case "Q":
                        W_Queen_BB.set(i * 8 + j);
                        break;
                    case "A":
                        W_King_BB.set(i * 8 + j);
                        break;
                    case "p":
                        B_Pawn_BB.set(i * 8 + j);
                        break;
                    case "r":
                        B_Rook_BB.set(i * 8 + j);
                        break;
                    case "k":
                        B_Knight_BB.set(i * 8 + j);
                        break;
                    case "b":
                        B_Bishop_BB.set(i * 8 + j);
                        break;
                    case "q":
                        B_Queen_BB.set(i * 8 + j);
                        break;
                    case "a":
                        B_King_BB.set(i * 8 + j);
                        break;
                }
            }
        }
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
