package com.github.entity.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class LobbyMessage {

    @Getter
    public String id;

    public LobbyMessage(){}

    public LobbyMessage(String id){
        this.id = id;
    }


}
