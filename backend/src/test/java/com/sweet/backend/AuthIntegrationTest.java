package com.sweet.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweet.backend.dto.LoginRequestDto;
import com.sweet.backend.dto.UserRegistrationDto;
import com.sweet.backend.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        // Clean up the database after each test to ensure a clean state
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterNewUser() throws Exception {
        UserRegistrationDto userDto = new UserRegistrationDto("testuser", "password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                        .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldFailWhenUsernameIsTooShort() throws Exception {
        UserRegistrationDto userDto = new UserRegistrationDto("a", "password123"); // invalid username

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailWhenPasswordIsTooShort() throws Exception {
        UserRegistrationDto userDto = new UserRegistrationDto("validUser", "123"); // invalid password

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Sql(statements = "DELETE FROM users WHERE username = 'testuser'")
    void shouldLoginAndReturnToken() throws Exception {
        // Step 1: Register
        UserRegistrationDto userDto = new UserRegistrationDto("testuser", "password123");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                        .with(csrf())) // This is the corrected line
                .andExpect(status().isCreated());

        // Step 2: Login
        LoginRequestDto loginDto = new LoginRequestDto("testuser", "password123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto))
                        .with(csrf())) // This is the corrected line
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

}