package com.ahmadmouslimani.transaction.service;

import com.ahmadmouslimani.transaction.dto.TransactionRequestDTO;
import com.ahmadmouslimani.transaction.entity.TransactionRejection;

public interface TransactionRejectionService {
    TransactionRejection createTransactionRejection(TransactionRejection transactionRejection);
}
