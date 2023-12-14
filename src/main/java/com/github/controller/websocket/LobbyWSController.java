package com.github.controller.websocket;

import com.github.entity.websocket.LobbyMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class LobbyWSController {

    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public LobbyWSController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/test")
    public String test(LobbyMessage message) {
        System.out.println("LobbyController: " + message.id);
        return "Test" + message.id;
    }
}
