package com.github.engine.generator;

import com.github.engine.Bitboard;
import com.github.engine.interfaces.IBoard;
import com.github.engine.interfaces.IGenerator;
import com.github.engine.move.Move;
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
    public List<Integer> generate(int color, Move move) {
        List<Integer> moves = new ArrayList<>();

        long[] ownBoard = (color == 0 ? boardWhite: boardBlack);
        long ownPieces = (ownBoard[0] | ownBoard[1] | ownBoard[2] | ownBoard[3] | ownBoard[5]);

        int queenIndex = move.getFrom().getIndex();
        long queenPosition = 1L << queenIndex;

        // TODO Create possible moves



        return moves;
    }

}
