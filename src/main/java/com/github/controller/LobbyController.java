package com.github.controller;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class LobbyController {

    /*@PostMapping("/lobby/create")
    public ResponseEntity<LobbyModel> createLobby(){
        return "Hello, World!";
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserModel> getUserByID(@PathVariable("id")String id){
        Optional<UserModel> user = userRepository.findById(id);

        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }*/
}
