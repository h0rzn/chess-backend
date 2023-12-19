package com.github.model.debug;

import com.github.engine.models.MoveInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ResponseModel {
    private Integer id;
    private MoveInfo moveInfo;
}
