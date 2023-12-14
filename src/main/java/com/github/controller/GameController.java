package com.github.controller;

import com.github.entity.GameEntity;
import com.github.entity.LobbyEntity;
import com.github.model.GameModel;
import com.github.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    private GameService gameService;
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public GameController(GameService gameService, SimpMessagingTemplate messagingTemplate) {
        this.gameService = gameService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/game")
    public String game() {
        return null;
    }

    @PostMapping("/game")
    public ResponseEntity<GameEntity> createGame(@RequestBody GameModel gameModel) {
        GameEntity gameEntity = gameService.createGame(gameModel);
        System.out.println("GameLobby: " + gameEntity.getLobbyId());
        messagingTemplate.convertAndSend("/topic/lobby/" + gameEntity.getLobbyId(), "Joined");
        return new ResponseEntity<>(gameEntity, HttpStatus.CREATED);
    }
}
