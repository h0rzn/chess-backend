package com.github.engine.interfaces;

import com.github.engine.models.MoveInfo;
import com.github.engine.move.Move;

import java.util.List;

public interface IGame {
    MoveInfo execute(IUserAction action);
    int getActiveColor();
}
