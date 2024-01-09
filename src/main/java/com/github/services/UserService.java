package com.github.services;

import com.github.entity.UserEntity;
import com.github.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing users
 */
@Service
public class UserService {

    private UserRepository userRepository;

    /**
     * Constructor, params get autowired (injected) by Spring
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Creates a new user and saves it to the database
     */
    public UserEntity saveUser(UserEntity user) {
        userRepository.save(user);
        return user;
    }

    /**
     * Gets a user from the database
     */
    public Optional<UserEntity> getUserByID(UUID id) {
        return userRepository.findById(id);

    }
}
