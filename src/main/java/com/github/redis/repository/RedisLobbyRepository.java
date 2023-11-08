package com.github.redis.repository;

import com.github.entity.LobbyEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisLobbyRepository extends CrudRepository<LobbyEntity, String> {
}
