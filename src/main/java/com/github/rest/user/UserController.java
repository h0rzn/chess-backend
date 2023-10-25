package com.github.rest.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user/{id}")
    public ResponseEntity<UserModel> getUserByID(@PathVariable("id")int id){
        Optional<UserModel> user = userRepository.findById(id);

        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/user")
    public ResponseEntity<UserModel> createUser(@RequestBody UserModel userModel){
        UserModel user = userRepository.save(new UserModel(userModel.getUsername(), userModel.getPassword()));
        return ResponseEntity.ok(user);
    }


}
