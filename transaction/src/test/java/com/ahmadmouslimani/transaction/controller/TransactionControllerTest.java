package com.ahmadmouslimani.transaction.controller;

import com.ahmadmouslimani.transaction.dto.TransactionRequestDTO;
import com.ahmadmouslimani.transaction.dto.TransactionResponseDTO;
import com.ahmadmouslimani.transaction.enums.TransactionStatus;
import com.ahmadmouslimani.transaction.exception.GlobalExceptionHandler;
import com.ahmadmouslimani.transaction.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.containsString;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(TransactionController.class)
@Import(GlobalExceptionHandler.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("POST /create - Should return 201 when transaction is processed")
    void createTransaction_Returns201() throws Exception {
        // Arrange
        TransactionRequestDTO request = new TransactionRequestDTO(
                new BigDecimal("100.00"), "D", "1234123412341234"
        );
        TransactionResponseDTO response = new TransactionResponseDTO(
                UUID.randomUUID(), new BigDecimal("100.00"), Instant.now(),
                "D", UUID.randomUUID(), UUID.randomUUID(), TransactionStatus.APPROVED,
                false, "Transaction Success"
        );

        when(transactionService.createTransaction(any())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/transaction/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.details").value("Transaction Success"));
    }

    @Test
    @DisplayName("POST /create - Should return 400 for invalid Card Number")
    void createTransaction_InvalidCard_Returns400() throws Exception {
        //Arrange: Card number too short
        TransactionRequestDTO invalidRequest = new TransactionRequestDTO(
                new BigDecimal("100.00"), "D", "123"
        );

        //Act & Assert
        mockMvc.perform(post("/api/transaction/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(containsString("Card number must be 16 digits")));
    }

}