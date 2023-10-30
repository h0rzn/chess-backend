package com.github.redis.model;

import com.github.engine.Game;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


@RedisHash("Game")
public class GameWrapper {
    @Getter
    private final Game game;

    @Getter
    @Id
    private String id;

    public GameWrapper(Game game) {
        this.game = game;
    }

    public GameWrapper(String id, Game game) {
        this.game = game;
        this.id = id;
    }


}
