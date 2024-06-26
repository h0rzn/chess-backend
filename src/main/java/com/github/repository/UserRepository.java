package com.github.repository;


import com.github.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for managing users, Methods are autowired by Spring
 */
@Repository
public interface UserRepository extends CrudRepository<UserEntity, UUID> {
    UserEntity findByUsername(String username);
}
