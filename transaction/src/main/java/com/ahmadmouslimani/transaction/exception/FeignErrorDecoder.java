package com.ahmadmouslimani.transaction.exception;

import com.ahmadmouslimani.transaction.dto.ErrorResponseDTO;
import com.ahmadmouslimani.transaction.exception.account.AccountNotFoundException;
import com.ahmadmouslimani.transaction.exception.card.CardNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper;

    public FeignErrorDecoder() {
        this.objectMapper = new ObjectMapper();
        //Register JavaTimeModule to handle LocalDateTime in ErrorResponseDTO
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        String errorMessage = "External service error";

        //extract the custom message from the response body
        try (InputStream bodyIs = response.body().asInputStream()) {
            ErrorResponseDTO errorResponse = objectMapper.readValue(bodyIs, ErrorResponseDTO.class);
            errorMessage = errorResponse.errorMessage();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        return switch (response.status()) {
            case 404 -> handleNotFound(methodKey, errorMessage);
            case 422 -> new BusinessException(errorMessage);
            case 400 -> new BadRequestException(errorMessage);
            case 500, 502, 503, 504 -> new ExternalServiceException(
                    "Service " + extractServiceName(methodKey) + ". " + errorMessage
            );
            default -> new Exception("Generic Error: " + errorMessage);
        };
    }

    private Exception handleNotFound(String methodKey, String message) {
        if (methodKey.contains("CardFeignClient")) {
            return new CardNotFoundException(message);
        }
        if (methodKey.contains("AccountFeignClient")) {
            return new AccountNotFoundException(message);
        }

        return new ResourceNotFoundException(message);
    }

    private String extractServiceName(String methodKey) {
        if (methodKey.contains("CardFeignClient")) return "Card";
        if (methodKey.contains("AccountFeignClient")) return "Account";
        if (methodKey.contains("FraudFeignClient")) return "Fraud";
        return "EXTERNAL-SERVICE";
    }
}