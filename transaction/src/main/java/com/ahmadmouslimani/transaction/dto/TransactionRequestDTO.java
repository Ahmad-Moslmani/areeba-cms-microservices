package com.ahmadmouslimani.transaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(name = "TransactionRequest", description = "Schema to hold Transaction request information")
public record TransactionRequestDTO (
        @Schema(description = "Transaction Amount", example = "100.5")
        @NotNull(message = "transactionAmount is required")
        @Digits(integer = 17, fraction = 2, message = "Invalid amount format")
        @Positive
        BigDecimal transactionAmount,

        @Schema(description = "Transaction type must be either 'C' for Credit or 'D' for Debit", example = "C")
        @NotNull(message = "transactionType is required")
        @Pattern(regexp = "^(C|D)$", message = "Transaction type must be either 'C' for Credit or 'D' for Debit")
        String transactionType,

        @Schema(description = "Card number (plain text)", example = "1234123412341234")
        @NotNull(message = "cardNumber is required")
        @Pattern(regexp = "^[0-9]{16}$", message = "Card number must be 16 digits")
        String cardNumber
) {}
