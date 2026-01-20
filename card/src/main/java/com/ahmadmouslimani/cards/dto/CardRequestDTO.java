package com.ahmadmouslimani.cards.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.UUID;

@Schema(name = "CardRequest", description = "Schema to hold card request information")
public record CardRequestDTO (
        @Schema(description = "Card number (plain text)", example = "1234123412341234")
        @NotBlank(message = "cardNumber is required")
        @Pattern(regexp = "^[0-9]{16}$", message = "Card number must be 16 digits")
        String cardNumber,

        @Schema(description = "Expiry date of the card", example = "2026-12-31")
        @NotNull(message = "Expiry date is required")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate expiry,

        @Schema(description = "Status of the card", example = "ACTIVE")
        @NotNull(message = "Status is required")
        @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "Status must be either 'ACTIVE' or 'INACTIVE'")
        String status,

        @Schema(description = "Account Id related to the card", example = "295a95ff-9a67-4a4f-bff1-7b8558368673")
        @NotNull(message = "accountId is required")
        UUID accountId
) {}
