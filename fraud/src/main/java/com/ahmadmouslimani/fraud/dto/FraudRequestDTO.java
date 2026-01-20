package com.ahmadmouslimani.fraud.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.util.UUID;

public record FraudRequestDTO(

        @Schema(description = "card Id", example = "295a95ff-9a67-4a4f-bff1-7b8558368673")
        @NotNull(message = "cardId is required")
        UUID cardId,

        @Schema(description = "Amount", example = "100.5")
        @NotNull(message = "Amount is required")
        @Digits(integer = 17, fraction = 2, message = "Invalid amount format")
        BigDecimal amount
) {}