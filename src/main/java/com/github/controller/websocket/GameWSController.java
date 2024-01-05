package com.github.controller.websocket;

import com.github.engine.models.MoveInfo;
import com.github.engine.move.Move;
import com.github.entity.websocket.LobbyMessage;
import com.github.model.debug.GameDebugModel;
import com.github.model.debug.GameMoveModel;
import com.github.model.debug.MoveDebugModel;
import com.github.model.debug.MoveInfoResponseModel;
import com.github.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class GameWSController {

    private SimpMessagingTemplate messagingTemplate;
    private GameService gameService;

    @Autowired
    public GameWSController(SimpMessagingTemplate messagingTemplate, GameService gameService) {
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
        System.out.println("[LOBBY-WS::newGame] with fen: '"+message.getFen()+"'");
        String fen = message.getFen();
        try {
            if (fen.isEmpty()) {
                gameService.createDebugGame();

            } else {
                gameService.createDebugGame(fen);
            }
            messagingTemplate.convertAndSend("/topic/debug/game/", "done");
        } catch (Exception e) {
            System.out.println("[LOBBY-WS::createGame] ERROR: "+e);
            messagingTemplate.convertAndSend("/topic/debug/game/", "error");
        }
    }

    @MessageMapping("/game/move")
    public void receiveMove(GameMoveModel message){
        GameMoveModel gameMoveModel = message;
        System.out.println("[GAME-WS::receiveMove][" + message.getId() + "]"+message.getId()+  " move: " + message.getMove());

        String moveString = gameMoveModel.getMove();

        if(moveDebugModel.getPromoteTo() == -1){
            Move move = new Move(moveString);
            MoveInfo execute = gameService.makeMove(move);
            MoveInfoResponseModel responseModel = new MoveInfoResponseModel(message.getId(), execute);
            messagingTemplate.convertAndSend("/topic/debug/move/", responseModel);
            return;
        }
        MoveInfo execute = gameService.makeMove(moveDebugModel.getMove(), moveDebugModel.getPromoteTo());
        MoveInfoResponseModel responseModel = new MoveInfoResponseModel(message.getId(), execute);
        messagingTemplate.convertAndSend("/topic/debug/move/", responseModel);

    }
}
