package com.github.services;

import com.github.engine.Game;
import com.github.redis.model.GameWrapper;
import com.github.redis.repository.RedisGameRepository;

import java.util.UUID;

public class GameService {

    private final RedisGameRepository<Game> redisGameRepository;

    public GameService(RedisGameRepository<Game> redisGameRepository) {
        this.redisGameRepository = redisGameRepository;
        if (redisGameRepository == null) {
            throw new IllegalArgumentException("The RedisGameRepository cannot be null!");
        }
    }

    public void createGame(){
        Game game = new Game();
        UUID uuid = UUID.randomUUID();

        redisGameRepository.add(new GameWrapper<>(uuid.toString(), game));
    }
}
