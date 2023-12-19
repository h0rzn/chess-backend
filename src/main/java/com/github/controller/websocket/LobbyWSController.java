package com.github.controller.websocket;

import com.github.engine.models.MoveInfo;
import com.github.engine.move.Move;
import com.github.entity.websocket.LobbyMessage;
import com.github.model.debug.GameDebugModel;
import com.github.model.debug.MoveDebugModel;
import com.github.model.debug.ResponseModel;
import com.github.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Objects;

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

    @MessageMapping("/debug/newgame")
    public void receiveMessage(GameDebugModel message) {
        System.out.println("test");
            GameDebugModel gameDebugModel = message;

            String fenString = gameDebugModel.getFen();
            if(Objects.equals(fenString, "")){
                System.out.println("NullFen");
                try {
                    gameService.createDebugGame();
                    messagingTemplate.convertAndSend("/topic/debug/game/", "done");
                } catch (Exception e) {
                    e.printStackTrace();
                    messagingTemplate.convertAndSend("/topic/debug/game/", "error");
                }
            }
//        if(message instanceof MoveDebugModel){
//            MoveDebugModel moveDebugModel = (MoveDebugModel) message;
//
//            String moveString = moveDebugModel.getMove();
//
//            Move move = new Move(moveString);
//            return gameService.makeMove(move);
//        }
    }

    @MessageMapping("/debug/move")
    public void receiveMove(MoveDebugModel message){
        MoveDebugModel moveDebugModel = message;
        System.out.println("Move: " + message.getMove());

        String moveString = moveDebugModel.getMove();

        Move move = new Move(moveString);
        MoveInfo execute = gameService.makeMove(move);
        ResponseModel responseModel = new ResponseModel(message.getId(), execute);
        messagingTemplate.convertAndSend("/topic/debug/move/", responseModel);
    }

    @MessageMapping("/debug/loadfen")
    public void receiveFen(GameDebugModel message) throws Exception {
        GameDebugModel gameDebugModel = message;

        String fenString = gameDebugModel.getFen();
        
        gameService.getGameStorageDebug().loadFEN(fenString);
        messagingTemplate.convertAndSend("/topic/debug/fen/", "done");
    }
}
