package com.ahmadmouslimani.fraud.dto;

public record FraudResponseDTO(
    boolean isFraudulent,
    String rejectionReason
) {
    public static FraudResponseDTO approved() {
        return new FraudResponseDTO(false, "Approved");
    }

    public static FraudResponseDTO rejected(String reason) {
        return new FraudResponseDTO(true, reason);
    }
}