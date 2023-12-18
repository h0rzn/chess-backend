package com.github.controller.websocket;

import com.github.entity.websocket.LobbyMessage;
import com.github.model.debug.AbstractDebugModel;
import com.github.model.debug.GameDebugModel;
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

    @MessageMapping("/debug")
    public void receiveMessage(AbstractDebugModel message) {
        System.out.println("test");
        if(message instanceof GameDebugModel) {
            GameDebugModel gameDebugModel = (GameDebugModel) message;
            System.out.println("GameDebugModel: " + gameDebugModel.getFen());
        }
    }
}
