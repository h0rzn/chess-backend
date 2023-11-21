package com.github.controller.websocket;

import com.github.entity.websocket.LobbyMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class LobbyWSController {

    @MessageMapping("/test")
    public String test(LobbyMessage message) {
        System.out.println("LobbyController: " + message.id);
        return "Test" + message.id;
    }

}
