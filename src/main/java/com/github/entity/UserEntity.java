package com.github.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;

@RedisHash("User")
public class UserEntity {
    @Id
    @Getter
    @Setter
    private UUID id;

    @Getter
    @Setter
    private String username;

    public UserEntity(String username) {
        this.id = UUID.randomUUID();
        this.username = username;

    }

    public UserEntity() {
    }
}
