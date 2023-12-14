package com.github.engine.generator;

public record CheckRecord(
        boolean isCheck,
        long kingEscapes,
        long[] attackBoards,
        long enemyMoveCovered
)
