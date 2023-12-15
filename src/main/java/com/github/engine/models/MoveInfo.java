package com.github.engine.models;

import com.github.engine.move.Move;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public final class MoveInfo {
    @Setter
    @Getter
    private boolean legal;
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
}
