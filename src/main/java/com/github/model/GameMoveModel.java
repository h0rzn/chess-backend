package com.github.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class GameMoveModel{
    private Integer id;
    private String gameId;
    private String playerId;
    private String move;
    private Integer promoteTo;

}
