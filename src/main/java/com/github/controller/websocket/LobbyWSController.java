package com.github.controller.websocket;

import com.github.engine.models.MoveInfo;
import com.github.engine.move.Move;
import com.github.entity.websocket.LobbyMessage;
import com.github.model.debug.*;
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

    @MessageMapping("/debug/move")
    public void receiveMove(MoveDebugModel message){
        System.out.println("[LOBBY-WS::receiveMove][" + message.getId() + "]"+  " move: " + message.getMove()+ "promoteTo "+message.getPromoteTo());
        MoveInfo moveResult = gameService.makeMove(message);
        MoveInfoResponseModel responseModel = new MoveInfoResponseModel(message.getId(), moveResult);
        messagingTemplate.convertAndSend("/topic/debug/move/", responseModel);
    }

    @MessageMapping("/debug/loadfen")
    public void receiveFen(GameDebugModel message) throws Exception {
        GameDebugModel gameDebugModel = message;
        System.out.println("Message ID" + message.getId());

        String fenString = gameDebugModel.getFen();
        System.out.println("FenString: " + fenString);
        gameService.getGame().load(fenString);
        LoadFenResponseModel responseModel = new LoadFenResponseModel(message.getId(), gameService.game.getLastMoveFen());
        messagingTemplate.convertAndSend("/topic/debug/fen/", responseModel);
    }
}
