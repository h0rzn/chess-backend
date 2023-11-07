package com.github.controller;

import com.github.entity.UserEntity;
import com.github.model.User;
import com.github.repository.UserRepository;
import com.github.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    public UserController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserByID(@PathVariable("id")long id){
        User user = userService.getUserByID(id);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/user")
    public User saveUser(@RequestBody User user) {
        return userService.saveUser(user);
    }


}
