package com.github.engine.interfaces;

import com.github.engine.move.Move;
import com.github.engine.move.MoveType;

public interface IUserAction {
    // represents a move type
    // such as normal, promotion, ...
    MoveType getType();

    // get the actual move
    Move getMove();

    // providing piece type to promote
    // only when user action is a promotion
    default int promotoTo() {
        return -1;
    }
}
