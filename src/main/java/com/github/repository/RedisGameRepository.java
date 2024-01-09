package com.github.repository;

import com.github.entity.GameEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing games, Methods are autowired by Spring
 */
@Repository
public interface RedisGameRepository extends CrudRepository<GameEntity, String> {

}
