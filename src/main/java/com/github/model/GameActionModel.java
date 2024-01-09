package com.github.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Model for game actions (surrender, draw)
 */
@Getter
@AllArgsConstructor
public class GameActionModel {
    private Integer id;
    private String gameId;
    private String playerId;
    private String action;

}
