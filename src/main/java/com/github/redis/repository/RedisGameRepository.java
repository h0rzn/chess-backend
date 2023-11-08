package com.github.redis.repository;

import com.github.entity.GameLobby;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisGameRepository extends CrudRepository<GameLobby, String> {

}
