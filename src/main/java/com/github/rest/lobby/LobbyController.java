package com.github.rest.lobby;

import com.github.redis.model.LobbyModel;
import com.github.rest.user.UserModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

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
