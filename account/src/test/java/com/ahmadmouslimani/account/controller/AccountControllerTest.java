package com.ahmadmouslimani.account.controller;

import com.ahmadmouslimani.account.dto.AccountRequestDTO;
import com.ahmadmouslimani.account.dto.AccountResponseDTO;
import com.ahmadmouslimani.account.exception.BusinessException;
import com.ahmadmouslimani.account.exception.GlobalExceptionHandler;
import com.ahmadmouslimani.account.exception.ResourceNotFoundException;
import com.ahmadmouslimani.account.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.eq;

import java.util.UUID;

@WebMvcTest(AccountController.class)
@Import(GlobalExceptionHandler.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    private final UUID fixedId = UUID.randomUUID();
    private final BigDecimal initial_balance = new BigDecimal("100.00");

    @Test
    @DisplayName("POST /create - Success 201")
    void createAccount_ValidRequest_ReturnsCreated() throws Exception {
        AccountRequestDTO request = new AccountRequestDTO("ACTIVE", initial_balance);
        AccountResponseDTO response = new AccountResponseDTO(fixedId, "ACTIVE", initial_balance);

        when(accountService.createAccount(any(AccountRequestDTO.class))).thenReturn(response);

        //Act & Assert
        mockMvc.perform(post("/api/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(fixedId.toString()))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.balance").value(100.00));
    }

    @Test
    @DisplayName("GET /{id} - Success 200")
    void getAccountById_ValidId_ReturnsOk() throws Exception {
        AccountResponseDTO response = new AccountResponseDTO(fixedId, "ACTIVE", initial_balance);

        when(accountService.getAccountById(fixedId)).thenReturn(response);

        mockMvc.perform(get("/api/account/{id}", fixedId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(fixedId.toString()));
    }

    @Test
    @DisplayName("PATCH /balance - Success 200")
    void adjustBalance_ValidCredit_ReturnsOk() throws Exception {
        BigDecimal creditAmount = new BigDecimal("50.00");
        AccountResponseDTO response = new AccountResponseDTO(fixedId, "ACTIVE", new BigDecimal("150.00"));

        when(accountService.adjustBalance(eq(fixedId), any(BigDecimal.class), eq("C")))
                .thenReturn(response);

        mockMvc.perform(patch("/api/account/{id}/balance", fixedId)
                        .param("amount", creditAmount.toString())
                        .param("transactionType", "C"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(150.00));
    }

    @Test
    @DisplayName("POST /create - Validation Failure 400")
    void createAccount_InvalidStatus_ReturnsBadRequest() throws Exception {
        //Only ACTIVE or INACTIVE are allowed
        AccountRequestDTO invalidRequest = new AccountRequestDTO("INVALID_STATUS", initial_balance);

        mockMvc.perform(post("/api/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(containsString("Validation Failed")));
    }

    @Test
    @DisplayName("GET /{id} - Not Found 404")
    void getAccountById_NotFound_ReturnsNotFound() throws Exception {
        when(accountService.getAccountById(fixedId))
                .thenThrow(new ResourceNotFoundException("Account", "id", fixedId.toString()));

        mockMvc.perform(get("/api/account/{id}", fixedId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value(containsString("not found")));
    }

    @Test
    @DisplayName("PUT /{id} - Success 200")
    void updateAccount_Success_ReturnsOk() throws Exception {
        AccountRequestDTO updateRequest = new AccountRequestDTO("INACTIVE", new BigDecimal("500.00"));
        AccountResponseDTO response = new AccountResponseDTO(fixedId, "INACTIVE", new BigDecimal("500.00"));

        when(accountService.updateAccount(eq(fixedId), any(AccountRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/account/{id}", fixedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"))
                .andExpect(jsonPath("$.balance").value(500));
    }

    @Test
    @DisplayName("PATCH /balance - Business Failure 422")
    void adjustBalance_InsufficientFunds_ReturnsUnprocessableEntity() throws Exception {
        when(accountService.adjustBalance(any(), any(), any()))
                .thenThrow(new BusinessException("Insufficient funds or account not found"));

        mockMvc.perform(patch("/api/account/{id}/balance", fixedId)
                        .param("amount", "1000.00")
                        .param("transactionType", "D"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorMessage").value("Insufficient funds or account not found"));
    }

    @Test
    @DisplayName("DELETE /{id} - Success 204")
    void deleteAccount_Success_ReturnsNoContent() throws Exception {
        doNothing().when(accountService).deleteAccount(fixedId);

        mockMvc.perform(delete("/api/account/{id}", fixedId))
                .andExpect(status().isNoContent());

        verify(accountService, times(1)).deleteAccount(fixedId);
    }

    @Test
    @DisplayName("POST /create - Should return 400 Bad Request when amount is in Invalid Format)")
    void shouldFailWhenAmountIsInvalidInputFormat() throws Exception {
        String jsonRequest = """
        {
            "status": "ACTIVE",
            "balance": "abcd"
        }
    """;

        mockMvc.perform(post("/api/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(containsString("Invalid input format")));
    }

    @Test
    @DisplayName("POST /create - Should return 400 Bad Request when status is missing")
    void shouldFailWhenCardIdIsMissing() throws Exception {
        String jsonRequest = """
            {
                "balance": 100.00
            }
        """;

        mockMvc.perform(post("/api/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(containsString("Status is required")));
    }
}