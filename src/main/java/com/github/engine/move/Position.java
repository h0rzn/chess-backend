package com.github.engine.move;

import lombok.Getter;
import lombok.Setter;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Position {
    @Getter
    private int index;
    // pieceType is not passed to the constructor
    // but rather set during move processing
    @Getter
    @Setter
    private int pieceType;

    // noPiece returns true if no piece has been set
    // so pieceType is -1
    public boolean noPiece() {
        return pieceType == -1;
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
        this.index = index;
        this.pieceType = -1;
    }

    public Position(int index) {
        if (index < 0 || index > 63) {
            throw new IllegalArgumentException("Index must be between 0 and 63");
        }
        this.index = index;
        this.pieceType = -1;
    }

    public Position(int index, int pieceType) {
        this.index = index;
        this.pieceType = pieceType;
    }

    public Position(Position position) {
        this.index = position.index;
        this.pieceType = position.pieceType;
    }

    /*
    // Keep rowFunction and columnFunction for now
    // probably not needed
    static Function<Integer, Integer> rowFunction = index -> index / 8;

    //Gets the column by an index (0-64)
    static Function<Integer, Integer> columnFunction = index -> index % 8;
     */

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
