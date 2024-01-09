package com.github.model.debug;

import com.github.engine.models.MoveInfo;
import com.github.model.ResponseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveInfoResponseModel extends ResponseModel {
    private MoveInfo moveInfo;


    public MoveInfoResponseModel(Integer id, MoveInfo moveInfo) {
        super(id);
        this.moveInfo = moveInfo;
    }
}
