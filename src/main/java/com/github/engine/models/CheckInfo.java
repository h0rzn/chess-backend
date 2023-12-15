package com.github.engine.models;

// CheckInfo stores information about a check
public record CheckInfo(
        boolean isCheck,
        // squares that are legally reachable for the
        // king to resolve check
        long kingEscapes,
        // attacks of each enemy piece group that threaten
        // the players king
        long[] attackBoards,
        // combination of all enemy piece move generation
        // useful for checking if a move would put the king
        // (back) in check
        long enemyMoveCovered
) {}
