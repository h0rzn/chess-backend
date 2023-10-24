package com.github.engine.generator;

import com.github.engine.Bitboard;
import com.github.engine.IBoard;
import com.github.engine.SinglePiece;

import java.util.List;

public class Generator implements IBoard {
    private long[] boardWhite;
    private long[] boardBlack;
    private Bitboard bitboard;

    public Generator(Bitboard bitboard){
        this.bitboard = bitboard;
        this.boardWhite = bitboard.getBoardWhite();
        this.boardBlack = bitboard.getBoardBlack();
    }


    public List<Integer> generate(T2<T3, T3> t2, int color){
        int pieceID = Get(t2.left().index(), color);
        SinglePiece piece = SinglePiece.fromNumber(pieceID);
        switch (piece){
            case Pawn -> {
                PawnMoveGenerator pawnMove = new PawnMoveGenerator(bitboard);
                return pawnMove.generate(color, t2);
            }
            case Knight -> {
                KnightMoveGenerator knightMove = new KnightMoveGenerator(bitboard);
                return knightMove.generate(color, t2);
            }
            case King -> {
                KingMoveGenerator kingMove = new KingMoveGenerator(bitboard);
                return kingMove.generate(color, t2);
            }

        }
        return null;
    }


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
}
