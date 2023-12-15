package com.github.engine.models;

import com.github.GameState;
import com.github.engine.move.Move;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
    }
}
