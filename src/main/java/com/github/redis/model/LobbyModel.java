package com.github.redis.model;

import com.github.engine.Game;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("Lobby")
public class LobbyModel {
    @Getter
    @Id
    private String id;

    private String playerID;


}
