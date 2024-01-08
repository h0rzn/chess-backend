package com.github.controller;

import com.github.entity.GameEntity;
import com.github.entity.LobbyEntity;
import com.github.model.GameModel;
import com.github.services.GameService;
import com.github.services.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class GameController {

    private GameService gameService;
    private HistoryService historyService;
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public GameController(GameService gameService, SimpMessagingTemplate messagingTemplate, HistoryService historyService) {
        this.gameService = gameService;
        this.messagingTemplate = messagingTemplate;
        this.historyService = historyService;
    }

    @GetMapping("/game/{id}")
    public ResponseEntity<GameEntity> getGameByID(@PathVariable("id") String id){
        Optional<GameEntity> game = gameService.getGameOptional(id);
        return game.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/game")
    public ResponseEntity<GameEntity> createGame(@RequestBody GameModel gameModel) throws Exception {
        GameEntity gameEntity = gameService.createGame(gameModel);
        historyService.createHistory(gameEntity.getId());
        System.out.println("GameLobby: " + gameEntity.getLobbyId());
        messagingTemplate.convertAndSend("/topic/lobby/" + gameEntity.getLobbyId(), "Joined");
        return new ResponseEntity<>(gameEntity, HttpStatus.CREATED);
    }
}
