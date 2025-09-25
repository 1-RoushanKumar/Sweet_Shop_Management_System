package com.sweet.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweet.backend.dto.AuthResponseDto;
import com.sweet.backend.dto.LoginRequestDto;
import com.sweet.backend.dto.SweetDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SweetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private String getAuthToken() {
        LoginRequestDto loginDto = new LoginRequestDto("testuser", "password123");
        String baseUrl = "http://localhost:" + port;
        ResponseEntity<AuthResponseDto> response =
                restTemplate.postForEntity(baseUrl + "/api/auth/login", loginDto, AuthResponseDto.class);

        if (response.getBody() == null) {
            throw new RuntimeException("Login failed. Check test user exists and password is correct.");
        }
        return response.getBody().getToken();
    }

    @Sql(statements = {
            "DELETE FROM users WHERE username='testuser'",
            "INSERT INTO users (username, password) VALUES ('testuser', '" +
            "$2a$10$Dow1j.2yhv7zQWnJ1U0b5uqx91k7M1e7P9MB6uOkKcm0m3p7vLweW" + "')"
    })
    void setupTestUser() {}

    @Test
    void shouldAddSweetWhenAuthenticated() throws Exception {
        SweetDto newSweet = new SweetDto("Chocolate Bar", "Chocolate", 2.50, 100);

        String token = getAuthToken();

        mockMvc.perform(post("/api/sweets")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSweet)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldNotAddSweetWithoutToken() throws Exception {
        SweetDto newSweet = new SweetDto("Chocolate Cake", "Chocolatey", 4.50, 800);

        mockMvc.perform(post("/api/sweets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSweet)))
                .andExpect(status().isForbidden());
    }
}