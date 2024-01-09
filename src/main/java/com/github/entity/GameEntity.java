package com.github.entity;

import com.github.engine.Game;
import com.github.utils.ChessClock;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;

/**
 * Redis Entity for game
 */
@RedisHash("Game")
@AllArgsConstructor
@Getter
public class GameEntity {

    private final Game game;

    @Id
    private String id;

    private Integer lobbyId;

    private UUID player1;

    private UUID player2;

    private UUID whitePlayerId;

    private UUID blackPlayerId;

    private ChessClock chessClock;

}
