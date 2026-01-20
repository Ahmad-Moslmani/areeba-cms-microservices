package com.ahmadmouslimani.transaction.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record FraudRequestDTO(

    UUID cardId,
    BigDecimal amount
) {}