package com.github.controller.websocket;

import com.github.engine.models.MoveInfo;
import com.github.engine.move.Move;
import com.github.entity.websocket.LobbyMessage;
import com.github.exceptions.GameNotFoundException;
import com.github.model.debug.*;
import com.github.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GameWSController {

    private SimpMessagingTemplate messagingTemplate;
    private GameService gameService;

    @Autowired
    public GameWSController(SimpMessagingTemplate messagingTemplate, GameService gameService) {
        this.messagingTemplate = messagingTemplate;
        this.gameService = gameService;
    }

    @MessageMapping("/game/move")
    public void receiveMove(GameMoveModel message) throws GameNotFoundException {
        System.out.println("[Game-WS::receiveMove][" + message.getId() + "]"+  " move: " + message.getMove()+ "promoteTo "+message.getPromoteTo());
        ResponseModelRecord moveResult = gameService.makeGameMove(message);
        GameMoveResponseModel responseModel = new GameMoveResponseModel(message.getId(),moveResult.moveInfo(), message.getGameId(), message.getPlayerId(), moveResult.whiteTimeLeft(), moveResult.blackTimeLeft());
        messagingTemplate.convertAndSend("/topic/game/move/", responseModel);

    }
}
