package com.github.engine.interfaces;

import java.util.List;

public interface IGame {
    boolean makeMove(IBoard.T2<IBoard.T3, IBoard.T3> t2);

    int getColorToMove();

    List<Integer> getMoves();

    boolean isCheck(int color);

    boolean isCheckMate(int color);
}
