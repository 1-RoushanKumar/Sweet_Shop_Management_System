package com.sweet.backend.controller;

import com.sweet.backend.dto.UserRegistrationDto;
import com.sweet.backend.model.User;
import com.sweet.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserRegistrationDto userDto) {
        User newUser = userService.registerNewUser(userDto);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
}