package com.github.model;

import com.github.model.debug.ResponseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameActionModel {
    private Integer id;
    private String gameId;
    private String playerId;
    private String action;

}
