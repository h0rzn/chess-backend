package com.github.services;

import com.github.engine.Game;
import com.github.redis.model.GameWrapper;
import com.github.redis.repository.RedisGameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GameService {

    private final RedisGameRepository redisGameRepository;

    @Autowired
    public GameService(RedisGameRepository redisGameRepository) {
        this.redisGameRepository = redisGameRepository;
    }

    public GameWrapper createGame(){
        Game game = new Game();
        UUID uuid = UUID.randomUUID();

        return redisGameRepository.save(new GameWrapper(uuid.toString(), game));

    }
}
