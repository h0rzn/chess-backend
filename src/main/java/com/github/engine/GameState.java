package com.github.engine;

// GameState enum represents the state of the game
// and is used in the game class as well as the move info
// to handle irregular cases such as promotion that modify
// the default move-making flow
public enum GameState {
    DEFAULT,
    END_WHITE_IN_CHECKMATE,
    END_BLACK_IN_CHECKMATE
}
