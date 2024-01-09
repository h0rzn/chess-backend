package com.github.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Random;
import java.util.UUID;

/**
 * Redis Entity for lobby
 */
@RedisHash("Lobby")
@Getter
public class LobbyEntity {

    @Id
    private int id;

    private UUID playerUUID;

    public LobbyEntity(){
        this.id = generateRandomId();
    }

    public LobbyEntity(UUID playerUUID){
        this.playerUUID = playerUUID;
        this.id = generateRandomId();
    }

    public static int generateRandomId() {
        Random random = new Random();
        int number = random.nextInt(900000) + 100000;
        return number;
    }


}
