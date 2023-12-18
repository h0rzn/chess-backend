package com.github.controller.websocket;

import com.github.engine.models.MoveInfo;
import com.github.engine.move.Move;
import com.github.entity.websocket.LobbyMessage;
import com.github.model.debug.AbstractDebugModel;
import com.github.model.debug.GameDebugModel;
import com.github.model.debug.MoveDebugModel;
import com.github.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class LobbyWSController {

    private SimpMessagingTemplate messagingTemplate;
    private GameService gameService;

    @Autowired
    public LobbyWSController(SimpMessagingTemplate messagingTemplate, GameService gameService) {
        this.messagingTemplate = messagingTemplate;
        this.gameService = gameService;
    }

    @MessageMapping("/test")
    public String test(LobbyMessage message) {
        System.out.println("LobbyController: " + message.id);
        return "Test" + message.id;
    }

    @MessageMapping("/debug")
    public MoveInfo receiveMessage(AbstractDebugModel message) {
        System.out.println("test");
        if(message instanceof GameDebugModel) {
            GameDebugModel gameDebugModel = (GameDebugModel) message;

            String fenString = gameDebugModel.getFen();
            if(fenString == null){
                try {
                    gameService.createDebugGame();
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        if(message instanceof MoveDebugModel){
            MoveDebugModel moveDebugModel = (MoveDebugModel) message;

            String moveString = moveDebugModel.getMove();

            Move move = new Move(moveString);
            return gameService.makeMove(move);
        }
        return null;
    }
}
