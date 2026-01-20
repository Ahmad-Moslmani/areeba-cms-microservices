package com.ahmadmouslimani.cards.controller;

import com.ahmadmouslimani.cards.dto.CardRequestDTO;
import com.ahmadmouslimani.cards.dto.CardResponseDTO;
import com.ahmadmouslimani.cards.exception.CardAlreadyExistsException;
import com.ahmadmouslimani.cards.exception.GlobalExceptionHandler;
import com.ahmadmouslimani.cards.exception.ResourceNotFoundException;
import com.ahmadmouslimani.cards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(CardController.class)
@Import(GlobalExceptionHandler.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardService cardService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String valid_card = "1234123412341234";
    private final UUID account_id = UUID.randomUUID();

    @Test
    @DisplayName("POST /create - Success 201")
    void createCard_ValidRequest_ReturnsCreated() throws Exception {
        CardRequestDTO request = new CardRequestDTO(valid_card, LocalDate.now().plusYears(1), "ACTIVE", account_id);
        CardResponseDTO response = new CardResponseDTO(UUID.randomUUID(), request.expiry(), valid_card, "ACTIVE", account_id);

        when(cardService.createCard(any(CardRequestDTO.class))).thenReturn(response);

        //Act & Assert
        mockMvc.perform(post("/api/card/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cardNumber").value(valid_card))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("POST /create - Validation Failure 400 (Invalid Card Number)")
    void createCard_InvalidCard_ReturnsBadRequest() throws Exception {
        //Only 3 digits - fails @Pattern
        CardRequestDTO invalidRequest = new CardRequestDTO("123", LocalDate.now().plusYears(1), "ACTIVE", account_id);

        mockMvc.perform(post("/api/card/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(containsString("Card number must be 16 digits")));
    }

    @Test
    @DisplayName("GET /fetch - Success 200")
    void getCardByNumber_ValidCardNumber_ReturnsOk() throws Exception {
        CardResponseDTO response = new CardResponseDTO(UUID.randomUUID(), LocalDate.now(), valid_card, "ACTIVE", account_id);
        
        when(cardService.getCardByCardNumber(valid_card)).thenReturn(response);

        mockMvc.perform(get("/api/card/fetch")
                .param("cardNumber", valid_card))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardNumber").value(valid_card));
    }

    @Test
    @DisplayName("GET /fetch - Parameter Validation 400")
    void getCardByNumber_TooShort_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/card/fetch")
                .param("cardNumber", "12345"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").exists());
    }

    @Test
    @DisplayName("PATCH /activate - Success 200")
    void activateCard_Valid_ReturnsOk() throws Exception {
        CardResponseDTO response = new CardResponseDTO(UUID.randomUUID(), LocalDate.now(), valid_card, "ACTIVE", account_id);
        
        when(cardService.activateCard(valid_card)).thenReturn(response);

        mockMvc.perform(patch("/api/card/{cardNumber}/activate", valid_card))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("POST /create - Conflict 409 (Custom Exception)")
    void createCard_AlreadyExists_ReturnsConflict() throws Exception {
        CardRequestDTO request = new CardRequestDTO(valid_card, LocalDate.now().plusYears(1), "ACTIVE", account_id);
        
        when(cardService.createCard(any())).thenThrow(new CardAlreadyExistsException("Card already exists"));

        mockMvc.perform(post("/api/card/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage").value("Card already exists"));
    }

    @Test
    @DisplayName("GET /{id} - Not Found 404")
    void getCardById_NotFound_ReturnsNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        when(cardService.getCardById(randomId))
                .thenThrow(new ResourceNotFoundException("Card", "Id", randomId.toString()));

        mockMvc.perform(get("/api/card/{id}", randomId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value(containsString("not found")));
    }
}