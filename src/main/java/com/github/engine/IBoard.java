package com.github.engine;

import java.util.function.BiFunction;
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

    Function<String, Integer> columnToIndex = index -> switch (index) {
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
    BiFunction<Integer, Integer, Integer> indexFn = (rank, file) -> rank * 8 + file;

    //Converts t2 to t3
    Function<T2<String, Integer>, T3> t2ToT3 = value ->
            new T3(
                    /*row*/    value.right(),
                    /*column*/ columnToIndex.apply(value.left()),
                    /*index*/  indexFn.apply(value.right(), columnToIndex.apply(value.left())));


    //Holds two values, Column and Row (e3)
    record T2<T, B>(T left, B right) {
        public static <T, B> T2<T, B> of(T left, B right) { return new T2<>(left, right); }
    }



}
