package com.github.model.debug;

import com.github.engine.models.MoveInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameMoveResponseModel extends ResponseModel{
    private MoveInfo moveInfo;
    private String gameId;
    private String playerId;

    public GameMoveResponseModel(Integer id, MoveInfo moveInfo, String gameId, String playerId) {
        super(id);
        this.gameId = gameId;
        this.playerId = playerId;
        this.moveInfo = moveInfo;
    }
}
