package com.ahmadmouslimani.fraud.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.containsString;
import org.springframework.http.MediaType;


@SpringBootTest
@AutoConfigureMockMvc
class FraudControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /check - Should return 400 Bad Request when cardId is missing")
    void shouldFailWhenCardIdIsMissing() throws Exception {
        String jsonRequest = """
            {
                "amount": 100.00
            }
        """;

        mockMvc.perform(post("/api/fraud/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(containsString("cardId is required")));
    }

    @Test
    @DisplayName("POST /check - Should return 400 Bad Request when amount is missing")
    void shouldFailWhenAmountIsMissing() throws Exception {
        String jsonRequest = """
            {
                "cardId": "f0f2fda5-a7ff-46fd-b1b9-33ff4381d7a0"
            }
        """;

        mockMvc.perform(post("/api/fraud/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(containsString("Amount is required")));
    }

    @Test
    @DisplayName("POST /check - Should return 400 Bad Request when cardId is in Invalid Input Format")
    void shouldFailWhenCardIdIsInvalidInputFormat() throws Exception {
        String jsonRequest = """
            {
                "cardId": "abcd",
                "amount": 100.00
            }
        """;

        mockMvc.perform(post("/api/fraud/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(containsString("Invalid input format")));
    }


    @Test
    @DisplayName("POST /check - Should return 400 Bad Request when amount is in Invalid Input Format")
    void shouldFailWhenAmountIsInvalidInputFormat() throws Exception {
        String jsonRequest = """
            {
                "cardId": "f0f2fda5-a7ff-46fd-b1b9-33ff4381d7a0",
                "amount": "abcd"
            }
        """;

        mockMvc.perform(post("/api/fraud/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(containsString("Invalid input format")));
    }
}