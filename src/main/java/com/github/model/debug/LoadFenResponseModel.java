package com.github.model.debug;


import com.github.model.ResponseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoadFenResponseModel extends ResponseModel {
    private String fen;
    public LoadFenResponseModel(Integer id, String fen) {
        super(id);
        this.fen = fen;
    }
}
