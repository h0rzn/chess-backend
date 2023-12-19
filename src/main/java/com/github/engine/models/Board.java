package com.github.engine.models;

public record Board(
        // needed to uniquely identify different parsed boards
        String name,
        // description of the board constellation and setup
        String description,
        long[] setWhite,
        long[] setBlack,
        int activeColor,
        int halfMoveClock,
        int fullMoveClock
) {}
