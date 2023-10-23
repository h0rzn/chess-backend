package com.github.engine.generator;

import com.github.engine.IBoard;

import java.util.List;

public interface IGenerator {
    List<Integer> generate(int color, IBoard.T2<IBoard.T3, IBoard.T3> t2);
}
