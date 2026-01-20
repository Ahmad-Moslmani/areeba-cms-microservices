package com.ahmadmouslimani.account.controller;

import com.ahmadmouslimani.account.dto.AccountRequestDTO;
import com.ahmadmouslimani.account.dto.AccountResponseDTO;
import com.ahmadmouslimani.account.dto.ErrorResponseDTO;
import com.ahmadmouslimani.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/account")
@Validated
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Account API", description = "Operations for managing account")
public class AccountController {
    private final AccountService accountService;


    @Operation(
            summary = "Create a new Account",
            description = "Creates a new account in the system based on the provided details. Returns the fully initialized account object."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Account created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PostMapping("/create")
    public ResponseEntity<AccountResponseDTO> createAccount(@RequestBody @Valid AccountRequestDTO request) {
        log.debug("Request Create Account");
        AccountResponseDTO accountResponseDTO = accountService.createAccount(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(accountResponseDTO);
    }


    @Operation(
            summary = "Fetch Account Details",
            description = "Retrieves full account information using its unique Id."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Account found and returned successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No Account found with the provided ID",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> getAccountById(@PathVariable UUID id) {
        log.debug("Request Account details for ID: {}", id);
        AccountResponseDTO accountResponseDTO = accountService.getAccountById(id);
        return ResponseEntity.status(HttpStatus.OK).body(accountResponseDTO);
    }


    @Operation(
            summary = "Update Account Details",
            description = "Updates the status and balance of an existing account identified by its UUID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Account updated successfully",
                    content = @Content(schema = @Schema(implementation = AccountResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No Account found with the provided ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> updateAccount(
            @PathVariable UUID id,
            @RequestBody @Valid AccountRequestDTO request) {

        log.debug("Request updateAccount with ID: {}", id);
        AccountResponseDTO updatedAccount = accountService.updateAccount(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(updatedAccount);
    }


    @Operation(
            summary = "Adjust Account Balance",
            description = "REST API to adjust account balance based on a accountId, amount, and transactionType"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Balance updated successfully",
                    content = @Content(
                            schema = @Schema(implementation = AccountResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account ID not found in the system",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PatchMapping("/{id}/balance")
    public ResponseEntity<AccountResponseDTO> adjustBalance(
            @PathVariable UUID id,
            @RequestParam BigDecimal amount,
            @RequestParam String transactionType) {
        log.debug("Request adjustBalance for accountId: {}", id);
        AccountResponseDTO updatedAccount = accountService.adjustBalance(id, amount, transactionType);
        return ResponseEntity.ok(updatedAccount);
    }


    @Operation(
            summary = "Delete an Account",
            description = "Deletes an existing account. Returns no content on success."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Account deleted successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "No Account found with the provided ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable UUID id) {
        log.debug("Request deleteAccount with accountId: {}", id);
        accountService.deleteAccount(id);

        return ResponseEntity.noContent().build();
    }
}
