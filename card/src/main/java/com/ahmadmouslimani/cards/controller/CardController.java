package com.ahmadmouslimani.cards.controller;

import com.ahmadmouslimani.cards.dto.CardRequestDTO;
import com.ahmadmouslimani.cards.dto.CardResponseDTO;
import com.ahmadmouslimani.cards.dto.ErrorResponseDTO;
import com.ahmadmouslimani.cards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/card")
@Validated
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Card API", description = "Operations for managing cards")
public class CardController {

    private final CardService cardService;


    @Operation(
            summary = "Create a new card",
            description = "Creates a new card in the system based on the provided details. Returns the fully initialized card object."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Card created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CardResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Card already exists",
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
    public ResponseEntity<CardResponseDTO> createCard(@RequestBody @Valid CardRequestDTO request) {
        log.debug("Request Create Card");
        CardResponseDTO cardResponseDTO = cardService.createCard(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cardResponseDTO);
    }




    @Operation(
            summary = "Fetch Card Details by Card Number",
            description = "Retrieves card information using the 16-digit provided as a query parameter."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Card found successfully",
                    content = @Content(
                            schema = @Schema(implementation = CardResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid card number format",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No card found with the provided card number",
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
    @GetMapping("/fetch")
    public ResponseEntity<CardResponseDTO> getCardByCardNumber(
            @RequestParam
            @Parameter(description = "The 16-digit card number", example = "1234123412341234")
            @Pattern(regexp = "^[0-9]{16}$", message = "Card number must be 16 digits")
            String cardNumber) {

        log.debug("Request fetching details for card number: {}", cardNumber.substring(cardNumber.length() - 4));

        CardResponseDTO cardResponseDTO = cardService.getCardByCardNumber(cardNumber);
        return ResponseEntity.ok(cardResponseDTO);
    }




    @Operation(
            summary = "Fetch Card Details by Id",
            description = "Retrieves full card information using its unique Id."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Card found and returned successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CardResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No card found with the provided ID",
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
    public ResponseEntity<CardResponseDTO> getCardById(@PathVariable UUID id) {
        log.debug("Request fetching card details for ID: {}", id);
        CardResponseDTO cardResponseDTO = cardService.getCardById(id);
        return ResponseEntity.status(HttpStatus.OK).body(cardResponseDTO);
    }



    @Operation(
            summary = "Activate Card",
            description = "REST API to activate card based on a Card Number"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Card activated successfully",
                    content = @Content(
                            schema = @Schema(implementation = CardResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Card number not found in the system",
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
    @PatchMapping("/{cardNumber}/activate")
    public ResponseEntity<CardResponseDTO> activateCard(
            @PathVariable
            @Parameter(description = "The 16-digit card number", example = "1234123412341234")
            @Pattern(regexp = "^[0-9]{16}$", message = "Card number must be 16 digits")
            String cardNumber) {
        log.debug("Request Card Activation for card number: {}", cardNumber.substring(cardNumber.length() - 4));
        CardResponseDTO updatedCard = cardService.activateCard(cardNumber);
        return ResponseEntity.ok(updatedCard);
    }




    @Operation(
            summary = "Deactivate Card",
            description = "REST API to deactivate card based on a Card Number"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Card deactivated successfully",
                    content = @Content(
                            schema = @Schema(implementation = CardResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Card number not found in the system",
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
    @PatchMapping("/{cardNumber}/deactivate")
    public ResponseEntity<CardResponseDTO> deactivateCard(
            @PathVariable
            @Parameter(description = "The 16-digit card number", example = "1234123412341234")
            @Pattern(regexp = "^[0-9]{16}$", message = "Card number must be 16 digits")
            String cardNumber) {
        log.debug("Request Card Deactivation for card number: {}", cardNumber.substring(cardNumber.length() - 4));
        CardResponseDTO updatedCard = cardService.deactivateCard(cardNumber);
        return ResponseEntity.ok(updatedCard);
    }

}
