package com.sweet.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.sweet.backend.dto.SweetDto;
import com.sweet.backend.dto.UserRegistrationDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SweetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Sql(statements = "DELETE FROM users WHERE username = 'testuser'")
    void shouldAddSweetWhenAuthenticated() throws Exception {
        // Step 1: Register a user to ensure it exists
        UserRegistrationDto userDto = new UserRegistrationDto("testuser", "password123");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                        .with(csrf()))
                .andExpect(status().isCreated());

        // Step 2: Perform login and capture the token from the response
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\", \"password\":\"password123\"}")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        String token = JsonPath.read(loginResult.getResponse().getContentAsString(), "$.token");

        // Step 3: Use the captured token for a protected API call
        SweetDto newSweet = new SweetDto("Chocolate Bar", "Chocolate", 2.50, 100);
        mockMvc.perform(post("/api/sweets")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSweet))
                        .with(csrf())) // CSRF is needed here as well for POST requests
                .andExpect(status().isCreated());
    }

    @Test
    void shouldNotAddSweetWithoutToken() throws Exception {
        SweetDto newSweet = new SweetDto("Chocolate Cake", "Chocolatey", 4.50, 800);

        mockMvc.perform(post("/api/sweets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSweet))
                        .with(csrf())) // CSRF is needed for the POST request
                .andExpect(status().isForbidden());
    }
}