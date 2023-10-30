package com.github.redis.repository;

import com.github.redis.model.LobbyModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisLobbyRepository extends CrudRepository<LobbyModel, String> {
}
