package com.github.engine.interfaces;

import com.github.engine.move.Move;
import com.github.engine.move.Position;

import java.util.List;

public interface IGenerator {
    //List<Integer> generate(int color, Position position);
    long generate(int color, Position position);
}
