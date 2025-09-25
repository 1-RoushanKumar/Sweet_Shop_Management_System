package com.sweet.backend.service;

import com.sweet.backend.dto.UserRegistrationDto;
import com.sweet.backend.model.User;
import com.sweet.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerNewUser(UserRegistrationDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists.");
        }
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword()); // Consider using a PasswordEncoder
        return userRepository.save(user);
    }
}