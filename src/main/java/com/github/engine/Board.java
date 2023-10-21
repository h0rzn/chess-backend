package com.github.engine;

import com.github.chessgame.Bitboard;

import java.util.BitSet;

public class Board {
    private Bitboard bitboard;

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

    public Board(){
        createBitBoards();
        System.out.println(W_Pawn_BB);
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
