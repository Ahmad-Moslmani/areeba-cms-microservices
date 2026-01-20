package com.ahmadmouslimani.transaction.service.impl;

import com.ahmadmouslimani.transaction.dto.*;
import com.ahmadmouslimani.transaction.entity.Transaction;
import com.ahmadmouslimani.transaction.entity.TransactionRejection;
import com.ahmadmouslimani.transaction.enums.AccountStatus;
import com.ahmadmouslimani.transaction.enums.CardStatus;
import com.ahmadmouslimani.transaction.enums.TransactionStatus;
import com.ahmadmouslimani.transaction.enums.TransactionType;
import com.ahmadmouslimani.transaction.exception.ResourceNotFoundException;
import com.ahmadmouslimani.transaction.exception.account.AccountInsufficientBalanceException;
import com.ahmadmouslimani.transaction.exception.card.ExpiredCardException;
import com.ahmadmouslimani.transaction.exception.account.InactiveAccountException;
import com.ahmadmouslimani.transaction.exception.card.InactiveCardException;
import com.ahmadmouslimani.transaction.mapper.TransactionMapper;
import com.ahmadmouslimani.transaction.repository.TransactionRepository;
import com.ahmadmouslimani.transaction.service.TransactionRejectionService;
import com.ahmadmouslimani.transaction.service.TransactionService;
import com.ahmadmouslimani.transaction.service.client.AccountFeignClient;
import com.ahmadmouslimani.transaction.service.client.CardFeignClient;
import com.ahmadmouslimani.transaction.service.client.FraudFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionRejectionService transactionRejectionService;
    private final AccountFeignClient accountFeignClient;
    private final CardFeignClient cardFeignClient;
    private final FraudFeignClient fraudClient;
    private final TransactionMapper mapper;


    @Override
    @Transactional
    public TransactionResponseDTO createTransaction(TransactionRequestDTO request) {
        CardResponseDTO card = cardFeignClient.getCardByCardNumber(request.cardNumber());
        AccountResponseDTO account = accountFeignClient.getAccountById(card.accountId());
        Transaction transaction = mapper.mapToEntity(request, card.id(), card.accountId());

        try {
            //Perform Card validation
            validateCard(card);
            //Perform Account validation
            validateAccount(account, request);

            //Perform fraud check
            FraudRequestDTO fraudRequestDTO = new FraudRequestDTO(
                    card.id(),
                    request.transactionAmount()
            );
            FraudResponseDTO fraudResponseDTO = fraudClient.checkFraud(fraudRequestDTO);

            if (fraudResponseDTO.isFraudulent()) {
                //Save as REJECTED + Fraud Flag
                return finalizeRejection(transaction, fraudResponseDTO.rejectionReason(), true);
            }

            // BusinessException from Account or Card services is re-thrown by the FeignErrorDecoder
            accountFeignClient.adjustBalance(
                    card.accountId(),
                    request.transactionAmount(),
                    request.transactionType()
            );

            //Transaction APPROVED
            transaction.setStatus(TransactionStatus.APPROVED);
            Transaction createdTransaction = transactionRepository.save(transaction);
            return mapper.mapToDto(createdTransaction);

        }catch (InactiveCardException | ExpiredCardException |
              InactiveAccountException | AccountInsufficientBalanceException ex){
            return finalizeRejection(transaction, ex.getMessage(), false);
        }
    }


    private void validateCard(CardResponseDTO card) {
        //Card should be Active
        if (CardStatus.INACTIVE.name().equals(card.status())) {
            throw new InactiveCardException();
        }
        //Card should not be Expired
        if (card.expiry().isBefore(LocalDate.now())) {
            throw new ExpiredCardException(card.expiry());
        }
    }

    private void validateAccount(AccountResponseDTO account, TransactionRequestDTO request) {
        //Account should be Active
        if (AccountStatus.INACTIVE.name().equals(account.status())) {
            throw new InactiveAccountException();
        }

        //Check sufficient funds only for Debits (D)
        boolean isDebit = TransactionType.D.name().equals(request.transactionType());
        if (isDebit) {
            boolean hasInsufficientBalance = account.balance().compareTo(request.transactionAmount()) < 0;
            if (hasInsufficientBalance) {
                throw new AccountInsufficientBalanceException();
            }
        }
    }


    private TransactionResponseDTO finalizeRejection(Transaction transaction, String reason, boolean isFraud) {
        Transaction createdTransaction = transactionRepository.save(transaction);

        TransactionRejection transactionRejection = new TransactionRejection();
        transactionRejection.setTransaction(createdTransaction);
        transactionRejection.setReason(reason);
        transactionRejection.setFraudulent(isFraud);
        transactionRejectionService.createTransactionRejection(transactionRejection);
        createdTransaction.setTransactionRejection(transactionRejection);

        return getTransactionById(transaction.getId());
    }

    @Override
    public TransactionResponseDTO getTransactionById(UUID id) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Transaction with Id"+ id.toString() +" not fount")
        );
        return mapper.mapToDto(transaction);
    }
}
