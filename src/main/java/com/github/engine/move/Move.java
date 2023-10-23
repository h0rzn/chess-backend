package com.github.engine.move;

public record Move(int from, int to, MoveType moveType, int pieceID){

    public static Move createMove(int from, int to, MoveType moveType, int pieceID){
        return new Move(from, to, moveType, pieceID);
    }

}
