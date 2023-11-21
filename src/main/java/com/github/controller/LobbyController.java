package com.github.controller;

import com.github.entity.LobbyEntity;
import com.github.entity.UserEntity;
import com.github.services.LobbyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
public class LobbyController {

    @Autowired
    private LobbyService lobbyService;

    @PostMapping("/lobby")
    public ResponseEntity<LobbyEntity> createLobby(@RequestBody LobbyEntity lobby) {
        LobbyEntity lobbyEntity = lobbyService.saveLobby(lobby);
        return new ResponseEntity<>(lobbyEntity, HttpStatus.CREATED);
    }

    @GetMapping("/lobby/{id}")
    public ResponseEntity<LobbyEntity> getLobbyByID(@PathVariable("id") Integer id){
        Optional<LobbyEntity> lobby = lobbyService.getLobbyByID(id);

        return lobby.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
