package com.ahmadmouslimani.transaction.dto;

import com.ahmadmouslimani.transaction.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponseDTO(
        UUID id,
        BigDecimal transactionAmount,
        Instant transactionDate,
        String transactionType,
        UUID accountId,
        UUID cardId,
        TransactionStatus status,
        boolean isFraudulent,
        String details
) {}
