package com.github.engine;

import com.github.engine.interfaces.IUserAction;
import com.github.engine.move.Move;
import com.github.engine.move.MoveType;
import lombok.Getter;

public class MoveAction implements IUserAction {
    @Getter
    private Move move;
    private int promoteToPiece;

    public MoveType getType() {
        return move.getMoveType();
    }

    public int promoteTo() {
        return promoteToPiece;
    }

    // Constructor for Normal Move
    public MoveAction(Move move) {
        this.move = move;
        this.promoteToPiece = -1;
    }

    // Constructor for Promotion Move
    public MoveAction(int promoteTo) {
        this.promoteToPiece = promoteTo;
        this.move = new Move(MoveType.Promotion);
    }
}
