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
    private List<String> captures;
    @Setter
    @Getter
    private String stateFEN;
    @Setter
    @Getter
    private String failMessage;
    @Getter
    private List<String> log;

    public void pushLog(String content) {
        this.log.add(content);
    }

    public void Fail(String message) {
        this.legal = false;
        this.failMessage = message;
    }

    public MoveInfo WithFailure(String message, Move move) {
        Fail(message);
        this.move = move;
        return this;
    }

    public MoveInfo WithSuccess(Move move, String updatedFen, List<String> captures) {
        this.legal = true;
        this.move = move;
        this.stateFEN = updatedFen;
        this.captures = captures;
        this.pushLog("++ move is legal and synced ++");

        return this;
    }

    @Override
    public String toString() {
        return "MoveInfo{" +
                "legal=" + legal +
                ", playerColor=" + playerColor +
                ", gameState=" + gameState +
                ", move=" + move.toString() +
                ", stateFEN='" + stateFEN + '\'' +
                ", failMessage='" + failMessage + '\'' +
                ", log=" + log +
                '}';
    }

    public MoveInfo() {
        this.gameState = GameState.UNKOWN;
        this.log = new ArrayList<String>();
    }
}
