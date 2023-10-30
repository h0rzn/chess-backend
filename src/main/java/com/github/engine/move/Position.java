package com.github.engine.move;

import lombok.Getter;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Position {
    @Getter
    private int row; //row
    @Getter
    private int column; //column
    @Getter
    private int index;

    public Position(int row, int column, int index) {
        this.row = row;
        this.column = column;
        this.index = index;
    }


    public Position(String position) {
        if (position.length() != 2) {
            throw new IllegalArgumentException("Position must be 2 characters long");
        }
        int file = columnToIndex.apply(position.substring(0, 1).toUpperCase());
        int rank = Integer.parseInt(position.substring(1, 2)) - 1;
        int index = indexFn.apply(rank, file);
        if (index < 0 || index > 63) {
            throw new IllegalArgumentException("Index must be between 0 and 63");
        }
        this.column = file;
        this.row = rank;
        this.index = index;
    }

    public Position(int index) {
        if (index < 0 || index > 63) {
            throw new IllegalArgumentException("Index must be between 0 and 63");
        }
        this.index = index;
        this.row = rowFunction.apply(index);
        this.column = columnFunction.apply(index);
    }

    public Position(Position position) {
        this.row = position.row;
        this.column = position.column;
        this.index = position.index;
    }

    static Function<Integer, Integer> rowFunction = index -> index / 8;
    //Gets the column by an index (0-64)
    static Function<Integer, Integer> columnFunction = index -> index % 8;

    static Function<String, Integer> columnToIndex = index -> switch (index) {
        case "A" -> 0;
        case "B" -> 1;
        case "C" -> 2;
        case "D" -> 3;
        case "E" -> 4;
        case "F" -> 5;
        case "G" -> 6;
        case "H" -> 7;
        default -> throw new IllegalStateException("Unexpected value: " + index);
    };
    static BiFunction<Integer, Integer, Integer> indexFn = (rank, file) -> rank * 8 + file;

}
