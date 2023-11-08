package com.github.controller;

import com.github.entity.UserEntity;
import com.github.repository.UserRepository;
import com.github.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserEntity> getUserByID(@PathVariable("id") UUID id){
        Optional<UserEntity> user = userService.getUserByID(id);

        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/user")
    public ResponseEntity<UserEntity> saveUser(@RequestBody UserEntity user) {
        UserEntity savedUser = userService.saveUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }


}
