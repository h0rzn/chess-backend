package com.github.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;

/**
 * Redis Entity for user
 */
@RedisHash("User")
@Getter
@Setter
public class UserEntity {
    @Id
    private UUID id;

    private String username;

    public UserEntity(String username) {
        this.id = UUID.randomUUID();
        this.username = username;
    }

    public UserEntity() {
    }
}
