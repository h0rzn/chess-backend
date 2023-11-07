package com.github.services;

import com.github.engine.Game;
import com.github.exceptions.GameNotFoundException;
import com.github.redis.model.GameWrapper;
import com.github.redis.repository.RedisGameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
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

    public Game getGame(String UUID) throws GameNotFoundException {
        if(UUID.isEmpty()){
            return null;
        }
        return Optional.of(redisGameRepository.findById(UUID).get()).map(GameWrapper::getGame).orElseThrow(GameNotFoundException::new);

    }
}