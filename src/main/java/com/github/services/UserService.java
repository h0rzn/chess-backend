package com.github.services;

import com.github.entity.UserEntity;
import com.github.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity saveUser(UserEntity user) {
        userRepository.save(user);
        return user;
    }

    public Optional<UserEntity> getUserByID(UUID id) {
        return userRepository.findById(id);

    }
}
