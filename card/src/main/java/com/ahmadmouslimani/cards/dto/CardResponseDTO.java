package com.ahmadmouslimani.cards.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

@Schema(name = "CardResponse", description = "Schema to hold card response information")
public record CardResponseDTO(
        @Schema(description = "Unique identifier for the card")
        UUID id,

        @Schema(description = "Expiry date of the card", example = "2026-12-31")
        LocalDate expiry,

        @Schema(description = "Card number", example = "1234123412341234")
        String cardNumber,

        @Schema(description = "Status of the card", example = "ACTIVE")
        String status,

        @Schema(description = "Account Id related to the card", example = "295a95ff-9a67-4a4f-bff1-7b8558368673")
        UUID accountId
) {}