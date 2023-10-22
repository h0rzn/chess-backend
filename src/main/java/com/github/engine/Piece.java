package com.github.engine;

public enum Piece {
    WPawn(1),
    WKnight(2),
    WBishop(3),
    WRook(4),
    WQueen(5),
    WKing(6),
    BPawn(7),
    BKnight(8),
    BBishop(9),
    BRook(10),
    BQueen(11),
    BKing(12),
    None(13)
    ;

    Piece(Integer number) {
    }

    public static Piece fromNumber(Integer number) {
        switch (number) {
            case 1:
                return WPawn;
            case 2:
                return WKnight;
            case 3:
                return WBishop;
            case 4:
                return WRook;
            case 5:
                return WQueen;
            case 6:
                return WKing;
            case 7:
                return BPawn;
            case 8:
                return BKnight;
            case 9:
                return BBishop;
            case 10:
                return BRook;
            case 11:
                return BQueen;
            case 12:
                return BKing;
            default:
                throw new IllegalArgumentException("Invalid piece number: " + number);
        }
    }
}
