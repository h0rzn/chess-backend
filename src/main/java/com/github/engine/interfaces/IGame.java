package com.github.engine.interfaces;

import com.github.engine.move.Move;

import java.util.List;

public interface IGame {
    boolean makeMove(Move move);

    int getColorToMove();

    List<Integer> getMoves();

    boolean isCheck(int color);

    boolean isCheckMate(int color);
}
