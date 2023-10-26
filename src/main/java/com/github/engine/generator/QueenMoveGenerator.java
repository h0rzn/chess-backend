package com.github.engine.generator;

import com.github.engine.Bitboard;
import com.github.engine.IBoard;
import com.github.engine.move.MoveType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class QueenMoveGenerator implements IBoard, IGenerator {

    private final long[] boardWhite;
    private final long[] boardBlack;

    @Getter
    private MoveType moveType;

    public QueenMoveGenerator(Bitboard board) {
        this.boardWhite = board.getBoardWhite();
        this.boardBlack = board.getBoardBlack();
    }

    @Override
    public List<Integer> generate(int color, T2<T3, T3> t2){
        List<Integer> moves = new ArrayList<>();

        long[] ownBoard = (color == 0 ? boardWhite: boardBlack);
        long ownPieces = (ownBoard[0] | ownBoard[1] | ownBoard[2] | ownBoard[3] | ownBoard[5]);

        int queenIndex = t2.left().index();
        long queenPosition = 1L << queenIndex;

        // TODO Create possible moves



        return moves;
    }

}
