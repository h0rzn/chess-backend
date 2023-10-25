package com.github.redis.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("Game")
public class GameWrapper<T> {
    @Getter
    private final T game;

    @Getter
    @Id
    private String id;

    public GameWrapper(T game) {
        this.game = game;
    }

    public GameWrapper(String id, T game) {
        this.game = game;
        this.id = id;
    }


}
