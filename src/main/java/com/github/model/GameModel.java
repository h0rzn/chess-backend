package com.github.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
 * Model for game creation
 */
@Getter
@AllArgsConstructor
public class GameModel {
    private UUID player1;
    private UUID player2;
    private Integer lobbyId;
}
