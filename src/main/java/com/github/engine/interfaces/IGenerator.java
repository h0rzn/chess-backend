package com.github.engine.interfaces;

import com.github.engine.move.Move;

import java.util.List;

public interface IGenerator {
    List<Integer> generate(int color, Move move);
}
