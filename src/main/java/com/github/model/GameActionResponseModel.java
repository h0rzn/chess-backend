package com.github.model;

import lombok.Getter;

/**
 * Model for game actions response (surrender, draw)
 */
@Getter
public class GameActionResponseModel extends ResponseModel {
    private String gameId;
    private String playerId;
    private String action;
    private String whoResigns;

    public GameActionResponseModel(Integer id, String gameId, String playerId, String action, String whoResigns) {
        super(id);
        this.gameId = gameId;
        this.playerId = playerId;
        this.action = action;
        this.whoResigns = whoResigns;
    }
}
