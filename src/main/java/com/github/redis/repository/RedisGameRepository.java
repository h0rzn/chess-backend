package com.github.redis.repository;

import com.github.redis.model.GameWrapper;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RedisGameRepository extends CrudRepository<GameWrapper, String> {

}
