package com.github.entity;

import com.github.engine.Game;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;


@RedisHash("Game")
@AllArgsConstructor
public class GameEntity {
    @Getter
    private final Game game;

    @Getter
    @Id
    private String id;

    @Getter
    private Integer lobbyId;

    @Getter
    private UUID player1;
    @Getter
    private UUID player2;



}
