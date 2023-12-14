package com.github.repository;

import com.github.entity.GameEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisGameRepository extends CrudRepository<GameEntity, String> {

}
