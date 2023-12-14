package com.github.engine.move;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


public class MoveInfo {
    @Setter
    @Getter
    private Move move;

    @Getter
    @Setter
    private String FailMessage;

    @Getter
    private List<String> log;
    public void pushLog(String content) {
        log.add(content);
    }
}
