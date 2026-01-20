package com.ahmadmouslimani.transaction.service.impl;

import com.ahmadmouslimani.transaction.entity.TransactionRejection;
import com.ahmadmouslimani.transaction.repository.TransactionRejectionRepository;
import com.ahmadmouslimani.transaction.service.TransactionRejectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionRejectionServiceImpl implements TransactionRejectionService {
    private final TransactionRejectionRepository transactionRejectionRepository;
    @Override
    public TransactionRejection createTransactionRejection(TransactionRejection transactionRejection) {
        return transactionRejectionRepository.save(transactionRejection);
    }
}
