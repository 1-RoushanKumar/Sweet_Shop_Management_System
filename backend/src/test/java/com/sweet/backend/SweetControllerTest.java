package com.sweet.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweet.backend.dto.SweetDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SweetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // We no longer need getAuthToken() as we will use MockMvc's built-in security test support.

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
        // Mock a user with the "USER" role
    void shouldAddSweetWhenUser() throws Exception {
        // This test should fail if only ADMINs can add sweets
        SweetDto newSweet = new SweetDto("Chocolate Bar", "Chocolate", 2.50, 100);
        mockMvc.perform(post("/api/sweets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSweet))
                        .with(csrf()))
                .andExpect(status().isForbidden()); // We expect a 403 Forbidden
    }

    @Test
    @WithMockUser(username = "testadmin", roles = {"ADMIN"})
        // Mock a user with the "ADMIN" role
    void shouldAddSweetWhenAdmin() throws Exception {
        SweetDto newSweet = new SweetDto("Chocolate Bar", "Chocolate", 2.50, 100);
        mockMvc.perform(post("/api/sweets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSweet))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Chocolate Bar"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"}) // Mock a regular user
    @Sql(statements = {
            "INSERT INTO sweet (id, name, category, price, quantity) VALUES (1, 'Caramel Chew', 'Chewy', 1.50, 50)"
    })
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, statements = "DELETE FROM sweet WHERE id = 1")
    void shouldSearchSweetsWhenUser() throws Exception {
        mockMvc.perform(get("/api/sweets/search")
                        .param("name", "Caramel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Caramel Chew"));
    }

    @Test
    @WithMockUser(username = "testadmin", roles = {"ADMIN"})
    @Sql(statements = "INSERT INTO sweet (id, name, category, price, quantity) VALUES (1, 'Fudge', 'Chocolate', 5.00, 20)")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, statements = "DELETE FROM sweet WHERE id = 1")
    void shouldUpdateSweetWhenAdmin() throws Exception {
        SweetDto updatedSweet = new SweetDto("Deluxe Fudge", "Chocolate", 6.00, 25);
        mockMvc.perform(put("/api/sweets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSweet))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Deluxe Fudge"));
    }

    @Test
    @WithMockUser(username = "testadmin", roles = {"ADMIN"})
    void shouldReturnNotFoundForNonExistentSweetUpdate() throws Exception {
        SweetDto updatedSweet = new SweetDto("Non-existent Sweet", "Category", 1.00, 1);
        mockMvc.perform(put("/api/sweets/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSweet))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testadmin", roles = {"ADMIN"})
    @Sql(statements = "INSERT INTO sweet (id, name, category, price, quantity) VALUES (1, 'Sweet To Delete', 'Test', 1.00, 1)")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, statements = "DELETE FROM sweet WHERE id = 1")
    void shouldDeleteSweetWhenAdmin() throws Exception {
        mockMvc.perform(delete("/api/sweets/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    @Sql(statements = "INSERT INTO sweet (id, name, category, price, quantity) VALUES (1, 'Sweet To Delete', 'Test', 1.00, 1)")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, statements = "DELETE FROM sweet WHERE id = 1")
    void shouldNotDeleteSweetWhenUser() throws Exception {
        mockMvc.perform(delete("/api/sweets/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"}) // We will perform the purchase as a regular user
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, statements = "DELETE FROM sweet")
    void shouldPurchaseSweetWhenInStock() throws Exception {
        // 1. Arrange: Create a sweet with a quantity greater than 0.
        // We use .with(user("testadmin").roles("ADMIN")) to temporarily
        // switch to an ADMIN user to create the sweet, as a regular USER can't.
        SweetDto sweetDto = new SweetDto("Gummy Bears", "Gummy", 3.00, 10);
        MvcResult result = mockMvc.perform(post("/api/sweets")
                        .with(user("testadmin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sweetDto))
                        .with(csrf())) // csrf is needed for POST requests
                .andExpect(status().isCreated())
                .andReturn();
        Long sweetId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        // 2. Act: Perform the purchase request as the original USER
        // The purchase endpoint is likely a POST, so csrf() is added.
        mockMvc.perform(post("/api/sweets/" + sweetId + "/purchase")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(9)); // 3. Assert: Verify the quantity decreased by 1
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    @Sql(statements = "INSERT INTO sweet (id, name, category, price, quantity) VALUES (1, 'Gummy Bears', 'Gummy', 3.00, 0)")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, statements = "DELETE FROM sweet")
    void shouldNotPurchaseSweetWhenOutOfStock() throws Exception {
        // Act: Attempt to purchase a sweet with a quantity of 0
        mockMvc.perform(post("/api/sweets/1/purchase")
                        .with(csrf()))
                .andExpect(status().isBadRequest()); // Assert that a 400 Bad Request is returned
    }
}