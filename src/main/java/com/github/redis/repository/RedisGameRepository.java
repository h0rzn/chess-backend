package com.github.redis.repository;

import com.github.redis.model.GameWrapper;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RedisGameRepository<T> {
    private final HashOperations<String, String, GameWrapper<T>> hashOperations;

    public RedisGameRepository(RedisTemplate<String, GameWrapper<T>> redisTemplate) {
        if (redisTemplate == null) {
            throw new IllegalArgumentException("The RedisTemplate cannot be null!");
        }
        hashOperations = redisTemplate.opsForHash();
    }

    public void add(String id, T game) {
        if (id == null || game == null) {
            throw new IllegalArgumentException("The game or the key cannot be null!");
        }
        add(new GameWrapper<>(id, game));
    }

    public void add(GameWrapper<T> game){
        if (game == null || game.getId() == null) {
            throw new IllegalArgumentException("GenericGameHandlerWrapper or the key cannot be null!");
        }

        hashOperations.put("Game", game.getId(), game);
    }

    public GameWrapper<T> get(String id) {
        if (id == null) {
            throw new IllegalArgumentException("The key cannot be null!");
        }
        return hashOperations.get("Game", id);
    }

    public void delete(String id) {
        if (id == null) {
            throw new IllegalArgumentException("The key cannot be null!");
        }
        hashOperations.delete("Game", id);
    }

    public List<GameWrapper<T>> getAll() {
        return hashOperations.values("Game");
    }


}
