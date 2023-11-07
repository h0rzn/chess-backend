package com.github.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("User")
public class UserEntity {
    @Id
    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    private String username;

    public UserEntity(String username) {
        this.username = username;

    }

    public UserEntity() {
    }
}
