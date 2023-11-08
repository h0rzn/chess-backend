package com.github.controller.websocket;

import com.github.entity.websocket.LobbyMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class LobbyController {

    @MessageMapping("/lobby/{id}")
    public String test(@DestinationVariable int id, LobbyMessage message) {
        System.out.println("LobbyController: " + id);
        return "Test" + id;
    }

}
