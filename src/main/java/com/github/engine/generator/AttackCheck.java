package com.github.engine.generator;

import com.github.engine.Bitboard;
import com.github.engine.Game;
import com.github.engine.interfaces.IBoard;
import com.github.engine.move.Move;
import jdk.jshell.spi.ExecutionControl;

import java.util.List;

public class AttackCheck implements IBoard {

    public static boolean isCheck(Bitboard bitboard, long kingPosition, int kingColor) {
        long[] kingsBoard = kingColor == 0 ? bitboard.getBoardWhite() : bitboard.getBoardBlack();
        long[] enemiesBoard = kingColor == 0 ? bitboard.getBoardBlack() : bitboard.getBoardWhite();
        long kingBoard = kingsBoard[5];

        // is king in check?
        if (!AttackCheck.isKingInCheck(kingPosition, enemiesBoard, kingColor)) {
            return false;
        }
        // generate legal moves for king
        int fakeFrom = 5;
        int fakeTo = 5;
        Move generatorMove = new Move(fakeFrom, fakeTo);
        KingMoveGenerator kingMoveGen = new KingMoveGenerator(bitboard);
        List<Integer> kingMoves = kingMoveGen.generate(kingColor, generatorMove);

        // check if any legal move resolves check
        // check if same-color pieces can resolve by: blocking or attacking attacker piece

        return true;
    }

    // Creates a copy of the bitboard and executes the move, then checks if the move resolves the check
    public static boolean doesMoveResolveCheck(Game game, Move move){
        Game gameCopy = game.copy();
        gameCopy.makeMove(move);

        long kingPosition = game.getColorToMove() == 0 ? gameCopy.getBoardWhite()[5] : gameCopy.getBoardBlack()[5];

        long[] enemyBoard = game.getColorToMove() == 0 ? gameCopy.getBoardBlack() : gameCopy.getBoardWhite();
        boolean isInCheck = isKingInCheck(kingPosition, enemyBoard, game.getColorToMove());

        return !isInCheck;

    }

    // Checks if the king is in check
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

    // Checks if the king is attacked by pawns
    public static boolean isAttackedByPawns(long kingBitBoard, long enemyPawns, int color){
        long attacks;
        if (color ==0) {
            attacks = (enemyPawns << 7) & NOT_H_FILE;
            attacks |= (enemyPawns << 9) & NOT_A_FILE;
        } else {
            attacks = (enemyPawns >> 7) & NOT_A_FILE;
            attacks |= (enemyPawns >> 9) & NOT_H_FILE;
        }

        return (kingBitBoard & attacks) != 0;
    }

    // Checks if the king is attacked by knights
    public static boolean isAttackedByKnights(long kingBitBoard, long enemyKnights){
        long spots = (enemyKnights >> 17) & NOT_H_FILE; // Springe 2 hoch, 1 rechts
        spots |= (enemyKnights >> 15) & NOT_A_FILE; // Springe 2 hoch, 1 links
        spots |= (enemyKnights >> 10) & NOT_GH_FILE; // Springe 1 hoch, 2 rechts
        spots |= (enemyKnights >> 6) & NOT_AB_FILE; // Springe 1 hoch, 2 links
        spots |= (enemyKnights << 17) & NOT_A_FILE; // Springe 2 runter, 1 rechts
        spots |= (enemyKnights << 15) & NOT_H_FILE; // Springe 2 runter, 1 links
        spots |= (enemyKnights << 10) & NOT_AB_FILE; // Springe 1 runter, 2 rechts
        spots |= (enemyKnights << 6) & NOT_GH_FILE;

        return (kingBitBoard & spots) != 0;
    }
}
