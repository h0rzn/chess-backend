package com.github.engine.interfaces;

import com.github.engine.move.Move;
import com.github.engine.move.MoveType;

// IUserAction is a generic wrapper around Move
// to abstract normal moves and promotion moves
// to a user action
public interface IUserAction {
    // represents a move type
    // such as normal, promotion, ...
    MoveType getType();

    // get the actual move
    Move getMove();

    // providing piece type to promote
    // only when user action is a promotion
    default int promoteTo() {
        return -1;
    }
}
