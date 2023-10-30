package com.github.redis.repository;

import com.github.redis.model.GameWrapper;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RedisGameRepository extends CrudRepository<GameWrapper, String> {

}
