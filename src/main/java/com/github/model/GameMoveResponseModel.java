package com.github.model;

import com.github.engine.models.MoveInfo;
import com.github.model.ResponseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameMoveResponseModel extends ResponseModel {
    private MoveInfo moveInfo;
    private String gameId;
    private String playerId;
    private long whiteTimeLeft;
    private long blackTimeLeft;

    public GameMoveResponseModel(Integer id, MoveInfo moveInfo, String gameId, String playerId, long whiteTimeLeft, long blackTimeLeft) {
        super(id);
        this.gameId = gameId;
        this.playerId = playerId;
        this.moveInfo = moveInfo;
        this.whiteTimeLeft = whiteTimeLeft;
        this.blackTimeLeft = blackTimeLeft;
    }
}
