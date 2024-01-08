package com.github.model;

import com.github.model.debug.ResponseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class GameActionResponseModel extends ResponseModel {
    private String gameId;
    private String playerId;
    private String action;

    public GameActionResponseModel(Integer id, String gameId, String playerId, String action) {
        super(id);
        this.gameId = gameId;
        this.playerId = playerId;
        this.action = action;
    }
}
