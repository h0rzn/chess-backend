package com.github.controller.websocket;

import com.github.exceptions.GameNotFoundException;
import com.github.model.*;
import com.github.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.UUID;

/**
 * Websocket Controller for game
 */
@Controller
public class GameWSController {

    private SimpMessagingTemplate messagingTemplate;
    private GameService gameService;

    /**
     * Constructor for GameWSController
     */
    @Autowired
    public GameWSController(SimpMessagingTemplate messagingTemplate, GameService gameService) {
        this.messagingTemplate = messagingTemplate;
        this.gameService = gameService;
    }

    /**
     * Receives move message from client and sends it to the game service
     */
    @MessageMapping("/game/move")
    public void receiveMove(GameMoveModel message) throws GameNotFoundException {
        System.out.println("[Game-WS::receiveMove][" + message.getId() + "]"+  " move: " + message.getMove()+ "promoteTo "+message.getPromoteTo());
        ResponseModelRecord moveResult = gameService.makeGameMove(message);
        GameMoveResponseModel responseModel = new GameMoveResponseModel(message.getId(),moveResult.moveInfo(), message.getGameId(), message.getPlayerId(), moveResult.whiteTimeLeft(), moveResult.blackTimeLeft());
        messagingTemplate.convertAndSend("/topic/game/move/", responseModel);
    }

    /**
     * Receives action message from client and sends it to the game service
     */
    @MessageMapping("/game/action")
    public void receiveAction(GameActionModel gameActionModel){
        System.out.println("[Game-WS::receiveAction][" + gameActionModel.getId() + "]"+  " action: " + gameActionModel.getAction() + " player: " + gameActionModel.getPlayerId());
        if (gameActionModel.getAction().equals("resign")) {
            boolean whiteResigns = gameService.isWhite(gameActionModel.getGameId(), gameActionModel.getPlayerId());
            gameService.setOver(gameActionModel.getGameId());
            if(whiteResigns){
                GameActionResponseModel gameActionResponseModel = new GameActionResponseModel(gameActionModel.getId(), gameActionModel.getGameId(), gameActionModel.getPlayerId(), gameActionModel.getAction(), "white");
                messagingTemplate.convertAndSend("/topic/game/action/", gameActionResponseModel);
                return;
            }
            GameActionResponseModel gameActionResponseModel = new GameActionResponseModel(gameActionModel.getId(), gameActionModel.getGameId(), gameActionModel.getPlayerId(), gameActionModel.getAction(), "black");
            messagingTemplate.convertAndSend("/topic/game/action/", gameActionResponseModel);
        }

    }
}
