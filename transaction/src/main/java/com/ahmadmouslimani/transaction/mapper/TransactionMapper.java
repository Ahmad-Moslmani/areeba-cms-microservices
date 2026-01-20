package com.ahmadmouslimani.transaction.mapper;

import com.ahmadmouslimani.transaction.dto.TransactionRequestDTO;
import com.ahmadmouslimani.transaction.dto.TransactionResponseDTO;
import com.ahmadmouslimani.transaction.entity.Transaction;
import com.ahmadmouslimani.transaction.enums.TransactionStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TransactionMapper {

    public Transaction mapToEntity(TransactionRequestDTO dto, UUID cardId, UUID accountId) {
        if (dto == null) return null;
        Transaction transaction = new Transaction();
        transaction.setTransactionAmount(dto.transactionAmount());
        transaction.setTransactionType(dto.transactionType());
        transaction.setAccountId(accountId);
        transaction.setCardId(cardId);
        transaction.setStatus(TransactionStatus.REJECTED);

        return transaction;
    }
    public TransactionResponseDTO mapToDto(Transaction transaction) {
        if (transaction == null) return null;
        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getTransactionAmount(),
                transaction.getTransactionDate(),
                transaction.getTransactionType(),
                transaction.getAccountId(),
                transaction.getCardId(),
                transaction.getStatus(),
                transaction.getTransactionRejection()!=null?
                        transaction.getTransactionRejection().isFraudulent():false,
                transaction.getTransactionRejection()!=null?
                        transaction.getTransactionRejection().getReason():"Transaction Success"
//                rejection != null && rejection.isFraudulent(),
//                rejection != null ? rejection.getReason() : "Transaction Success"
        );
    }
}
