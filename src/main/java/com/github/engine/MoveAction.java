package com.github.engine;

import com.github.engine.interfaces.IUserAction;
import com.github.engine.move.Move;
import com.github.engine.move.MoveType;
import lombok.Getter;

// MoveAction implements the user action
// and sets moveType specific data in the constructor
// which is not required as input
public class MoveAction implements IUserAction {
    @Getter
    private Move move;
    @Getter
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
    public MoveAction(String move, int promoteTo) {
        if (promoteTo > -1) {
            this.promoteToPiece = promoteTo;
            this.move = new Move(move, MoveType.Promotion);
        } else {
            this.move = new Move(move, MoveType.Normal);
        }
    }

}
