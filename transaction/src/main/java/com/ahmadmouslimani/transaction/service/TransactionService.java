package com.ahmadmouslimani.transaction.service;

import com.ahmadmouslimani.transaction.dto.TransactionRequestDTO;
import com.ahmadmouslimani.transaction.dto.TransactionResponseDTO;

import java.util.UUID;

public interface TransactionService {
    TransactionResponseDTO createTransaction(TransactionRequestDTO request);

    TransactionResponseDTO getTransactionById(UUID id);
}
