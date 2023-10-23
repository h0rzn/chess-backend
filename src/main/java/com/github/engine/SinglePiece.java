package com.github.engine;

public enum SinglePiece {
    Pawn(0),
    Knight(1),
    Bishop(2),
    Rook(3),
    Queen(4),
    King(5)
    ;


    SinglePiece(int i) {
    }

    public static SinglePiece fromNumber(Integer number) {
        switch (number) {
            case 0:
                return Pawn;
            case 1:
                return Knight;
            case 2:
                return Bishop;
            case 3:
                return Rook;
            case 4:
                return Queen;
            case 5:
                return King;
            default:
                throw new IllegalArgumentException("Invalid piece number: " + number);
        }
    }
}
