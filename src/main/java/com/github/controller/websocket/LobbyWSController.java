package com.github.controller.websocket;

import com.github.engine.models.MoveInfo;
import com.github.model.debug.*;
import com.github.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * Websocket Controller for game
 */
@Controller
public class LobbyWSController {

    private SimpMessagingTemplate messagingTemplate;
    private GameService gameService;

    /**
     * Constructor for GameWSController
     */
    @Autowired
    public LobbyWSController(SimpMessagingTemplate messagingTemplate, GameService gameService) {
        this.messagingTemplate = messagingTemplate;
        this.gameService = gameService;
    }

    /**
     * Receives new game message from client and sends it to the game service
     */
    @MessageMapping("/debug/newgame")
    public void receiveMessage(DebugGameModel message) {
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

    /**
     * Receives move message from client and sends it to the game service
     */
    @MessageMapping("/debug/move")
    public void receiveMove(DebugMoveModel message){
        System.out.println("[LOBBY-WS::receiveMove][" + message.getId() + "]"+  " move: " + message.getMove()+ "promoteTo "+message.getPromoteTo());
        MoveInfo moveResult = gameService.makeDebugMove(message);
        MoveInfoResponseModel responseModel = new MoveInfoResponseModel(message.getId(), moveResult);
        messagingTemplate.convertAndSend("/topic/debug/move/", responseModel);
    }

    /**
     * Receives fen message from client and sends it to the game service
     */
    @MessageMapping("/debug/loadfen")
    public void receiveFen(DebugGameModel message) throws Exception {
        DebugGameModel debugGameModel = message;
        System.out.println("Message ID" + message.getId());

        String fenString = debugGameModel.getFen();
        System.out.println("FenString: " + fenString);
        gameService.getGameDebug().load(fenString);
        LoadFenResponseModel responseModel = new LoadFenResponseModel(message.getId(), gameService.gameDebug.getLastMoveFen());
        messagingTemplate.convertAndSend("/topic/debug/fen/", responseModel);
    }
}
