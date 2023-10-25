package com.github.rest.game;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    @GetMapping("/game")
    public String game() {
        return "Hello, World!";
    }

    @PostMapping("/game")
    public String createGame(){
        return "Hello, World!";
    }
}
