package com.github.engine.models;

public record CheckInfo(
        boolean isCheck,
        long kingEscapes,
        long[] attackBoards,
        long enemyMoveCovered
) {}
