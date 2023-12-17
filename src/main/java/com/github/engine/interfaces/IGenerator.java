package com.github.engine.interfaces;

import com.github.engine.move.Move;
import com.github.engine.move.Position;

import java.util.List;

// IGenerator specifies the required function
// of a move generator class for a specific piece
public interface IGenerator {
    //List<Integer> generate(int color, Position position);
    long generate(Position position);
}
