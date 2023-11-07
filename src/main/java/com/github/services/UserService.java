package com.github.services;

import com.github.entity.UserEntity;
import com.github.model.User;
import com.github.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user, userEntity);
        userRepository.save(userEntity);
        return user;
    }

    public User getUserByID(long id) {
        UserEntity userEntity = userRepository.findById(id);
        User user = new User();
        BeanUtils.copyProperties(userEntity, user);
        return user;
    }
}
