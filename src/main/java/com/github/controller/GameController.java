package com.github.controller;

import com.github.entity.GameLobby;
import com.github.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    private GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/game")
    public String game() {
        GameLobby gameLobby = gameService.createGame();
        return "Create game: " + gameLobby.getId();
    }

    @PostMapping("/game")
    public String createGame(){
        return "Hello, World!";
    }
}
