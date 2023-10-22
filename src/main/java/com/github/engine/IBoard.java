package com.github.engine;

import java.util.function.Function;

public interface IBoard {

    //Gets the row by an index (0-64)
    Function<Integer, Integer> rowFunction = index -> index / 8;
    //Gets the column by an index (0-64)
    Function<Integer, Integer> columnFunction = index -> index % 8;

    //Tuple for holding a rank, file, and index
    //Makes it easier to get the rank and file from an index
    record T3(int rank, int file, int index) {
        static T3 of(int index) {
            return new T3(rowFunction.apply(index), columnFunction.apply(index), index);
        }
    }



}
