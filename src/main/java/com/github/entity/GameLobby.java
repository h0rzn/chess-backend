package com.github.entity;

import com.github.engine.Game;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


@RedisHash("Game")
public class GameLobby {
    @Getter
    private final Game game;

    @Getter
    @Id
    private String id;

    public GameLobby(Game game) {
        this.game = game;
    }

    public GameLobby(String id, Game game) {
        this.game = game;
        this.id = id;
    }


}
