package com.github.engine.move;

import lombok.Getter;
import lombok.Setter;

public class Move {
    @Getter
    private Position from;
    @Getter
    private Position to;
    // moveType is - just as pieceType - set during
    // move processing
    @Getter
    @Setter
    private MoveType moveType;

    public Move(int from, int to) {
        this.from = new Position(from);
        this.to = new Position(to);
        this.moveType = MoveType.Unkown;
    }

    public Move(String move) {
        if (move.length() != 5) {
            throw new IllegalArgumentException("Move must be 5 characters long");
        }
        this.from = new Position(move.split("-")[0]);
        this.to = new Position(move.split("-")[1]);
    }
    /*
    public Move(Position from, Position to) {
        this.from = from;
        this.to = to;
    }

    public Move(int from, int to) {
        this.from = new Position(from);
        this.to = new Position(to);
    }

    public Move(int fromRank, int fromFile, int toRank, int toFile, int index) {
        this.from = new Position(fromRank, fromFile, index);
        this.to = new Position(toRank, toFile, index);
    }

    public Move(int fromRank, int fromFile, int toRank, int toFile, int index, int piece) {
        this.from = new Position(fromRank, fromFile, index);
        this.to = new Position(toRank, toFile, index);
    }
    */
}
