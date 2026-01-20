package com.ahmadmouslimani.fraud.controller;

import com.ahmadmouslimani.fraud.dto.ErrorResponseDTO;
import com.ahmadmouslimani.fraud.dto.FraudRequestDTO;
import com.ahmadmouslimani.fraud.dto.FraudResponseDTO;
import com.ahmadmouslimani.fraud.service.FraudAuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fraud")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Fraud API", description = "Operations for testing Fraud service")
public class FraudAuditLogController {
    private final FraudAuditLogService fraudAuditLogService;

    @Operation(
            summary = "Fraud check",
            description = "Check it if transaction is fraudulent."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Transaction successfully checked for fraud",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FraudResponseDTO.class)
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
    @PostMapping("/check")
    public ResponseEntity<FraudResponseDTO> checkTransaction(@Valid @RequestBody FraudRequestDTO request) {
        FraudResponseDTO response = fraudAuditLogService.validateTransaction(request);

        return ResponseEntity.ok(response);
    }
}
