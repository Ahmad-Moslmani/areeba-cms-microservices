package com.ahmadmouslimani.account.dto;

import java.math.BigDecimal;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AccountResponse", description = "Schema to hold Account response information")
public record AccountResponseDTO(

    @Schema(description = "Unique identifier of the account")
    UUID id,

    @Schema(description = "Status of the account", example = "ACTIVE")
    String status,

    @Schema(description = "Current balance of the account", example = "100.5")
    BigDecimal balance
) {}