package com.github.engine.generator;

import com.github.engine.Bitboard;
import com.github.engine.IBoard;

public class AttackCheck implements IBoard {


    public static boolean doesMoveResolveCheck(Bitboard bitboard, IBoard.T2<IBoard.T3, IBoard.T3> t2){
        Bitboard bitboardCopy = bitboard.copy();
        bitboardCopy.makeMove(t2);

        long kingPosition = bitboard.getColorToMove() == 0 ? bitboardCopy.getBoardWhite()[5] : bitboardCopy.getBoardBlack()[5];

        long[] enemyBoard = bitboard.getColorToMove() == 0 ? bitboardCopy.getBoardBlack() : bitboardCopy.getBoardWhite();
        boolean isInCheck = isKingInCheck(kingPosition, enemyBoard, bitboard.getColorToMove());

        return !isInCheck;

    }
    public static boolean isKingInCheck(long kingPosition, long[] enemyBoard, int color){
        //long kingBitBoard = 1L << kingPosition;

        if(isAttackedByPawns(kingPosition, enemyBoard[0], color)){
            return true;
        }

        if(isAttackedByKnights(kingPosition, enemyBoard[1])){
            return true;
        }

        return false;

    }
    public static boolean isAttackedByPawns(long kingBitBoard, long enemyBitBoard, int color){
        long attacks;
        if (color ==0) {
            attacks = (enemyBitBoard << 7) & NOT_H_FILE;
            attacks |= (enemyBitBoard << 9) & NOT_A_FILE;
        } else {
            attacks = (enemyBitBoard >> 7) & NOT_A_FILE;
            attacks |= (enemyBitBoard >> 9) & NOT_H_FILE;
        }

        return (kingBitBoard & attacks) != 0;
    }

    public static boolean isAttackedByKnights(long kingBitBoard, long enemyBitBoard){
        long spots = ((long) enemyBitBoard >> 17) & NOT_H_FILE; // Springe 2 hoch, 1 rechts
        spots |= (enemyBitBoard >> 15) & NOT_A_FILE; // Springe 2 hoch, 1 links
        spots |= (enemyBitBoard >> 10) & NOT_GH_FILE; // Springe 1 hoch, 2 rechts
        spots |= (enemyBitBoard >> 6) & NOT_AB_FILE; // Springe 1 hoch, 2 links
        spots |= (enemyBitBoard << 17) & NOT_A_FILE; // Springe 2 runter, 1 rechts
        spots |= (enemyBitBoard << 15) & NOT_H_FILE; // Springe 2 runter, 1 links
        spots |= (enemyBitBoard << 10) & NOT_AB_FILE; // Springe 1 runter, 2 rechts
        spots |= (enemyBitBoard << 6) & NOT_GH_FILE;

        return (kingBitBoard & spots) != 0;
    }
}
