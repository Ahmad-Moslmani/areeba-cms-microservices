package com.ahmadmouslimani.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

@Schema(name = "AccountRequest", description = "Schema to hold Account request information")
public record AccountRequestDTO (
        @Schema(description = "Status of the account", example = "ACTIVE")
        @NotNull(message = "Status is required")
        @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "Status must be either 'ACTIVE' or 'INACTIVE'")
        String status,

        @Schema(description = "Balance of the account", example = "100.5")
        @Digits(integer = 17, fraction = 2, message = "Invalid balance format")
        BigDecimal balance
) {}
