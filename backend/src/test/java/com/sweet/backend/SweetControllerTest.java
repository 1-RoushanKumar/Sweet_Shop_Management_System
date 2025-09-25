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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

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

    @Test
    @Sql(statements = {
            "DELETE FROM users WHERE username = 'testuser'", // Added this line
            "INSERT INTO sweet (name, category, price, quantity) VALUES ('Caramel Chew', 'Chewy', 1.50, 50)",
            "INSERT INTO sweet (name, category, price, quantity) VALUES ('Mint Drop', 'Hard Candy', 2.00, 75)",
            "INSERT INTO sweet (name, category, price, quantity) VALUES ('Fudge', 'Chocolate', 5.00, 20)"
    })
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, statements = {
            "DELETE FROM sweet",
            "DELETE FROM users WHERE username = 'testuser'" // Also added this for post-test cleanup
    })
    void shouldSearchSweetsByName() throws Exception {
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

        // Step 3: Use the captured token for the search API call
        mockMvc.perform(get("/api/sweets/search")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .param("name", "Caramel")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Caramel Chew"));
    }

    // Helper method to get a valid JWT token
    private String getAuthToken() throws Exception {
        // Ensure user exists before trying to log in
        UserRegistrationDto userDto = new UserRegistrationDto("testuser", "password123");
        try {
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userDto))
                            .with(csrf()))
                    .andExpect(status().isCreated());
        } catch (Exception e) {
            // User might already exist, which is fine for the test
        }

        // Perform login and capture the token
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\", \"password\":\"password123\"}")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        return JsonPath.read(loginResult.getResponse().getContentAsString(), "$.token");
    }

    @Test
    @Sql(statements = "INSERT INTO sweet (name, category, price, quantity) VALUES ('Fudge', 'Chocolate', 5.00, 20)")
    void shouldUpdateSweetWhenAuthenticated() throws Exception {
        SweetDto updatedSweet = new SweetDto("Deluxe Fudge", "Chocolate", 6.00, 25);
        String token = getAuthToken();

        mockMvc.perform(put("/api/sweets/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSweet)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Deluxe Fudge"));
    }

}