package com.ahmadmouslimani.transaction.dto;

public record FraudResponseDTO(
    boolean isFraudulent,
    String rejectionReason
) {}