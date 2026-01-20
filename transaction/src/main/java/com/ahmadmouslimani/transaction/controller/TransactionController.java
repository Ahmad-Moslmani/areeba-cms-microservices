package com.ahmadmouslimani.transaction.controller;

import com.ahmadmouslimani.transaction.dto.ErrorResponseDTO;
import com.ahmadmouslimani.transaction.dto.TransactionRequestDTO;
import com.ahmadmouslimani.transaction.dto.TransactionResponseDTO;
import com.ahmadmouslimani.transaction.service.TransactionService;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Transaction API", description = "Operations for managing Transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @Operation(
            summary = "Create a new Transaction",
            description = "Creates a new Transaction in the system based on the provided details."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Transaction created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Card is INACTIVE",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
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
    public ResponseEntity<TransactionResponseDTO> createTransaction(@RequestBody @Valid TransactionRequestDTO request) {
        log.debug("Create Transaction");
        TransactionResponseDTO transactionResponseDTO = transactionService.createTransaction(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionResponseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getTransaction(@PathVariable UUID id) {
        log.debug("Request Get Transaction by Id: {}", id);
        TransactionResponseDTO transactionResponseDTO = transactionService.getTransactionById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(transactionResponseDTO);
    }
}
