package com.github.engine.models;

import com.github.GameState;
import com.github.engine.move.Move;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

// MoveInfo stores data about a single move
// move processing writes logs about the respective
// steps of the process to 'log'
public class MoveInfo {
    @Setter
    @Getter
    private boolean legal;
    @Getter
    @Setter
    private int playerColor;
    @Getter
    @Setter
    private GameState gameState;
    @Setter
    @Getter
    private Move move;
    @Setter
    @Getter
    private String failMessage;
    @Getter
    private List<String> log;

    public void pushLog(String content) {
        this.log.add(content);
    }

    public MoveInfo() {
        this.gameState = GameState.UNKOWN;
        this.log = new ArrayList<String>();
    }
}
