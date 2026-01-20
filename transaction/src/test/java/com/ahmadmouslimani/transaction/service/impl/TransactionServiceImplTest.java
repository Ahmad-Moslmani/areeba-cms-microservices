package com.ahmadmouslimani.transaction.service.impl;

import com.ahmadmouslimani.transaction.dto.*;
import com.ahmadmouslimani.transaction.entity.Transaction;
import com.ahmadmouslimani.transaction.enums.TransactionStatus;
import com.ahmadmouslimani.transaction.mapper.TransactionMapper;
import com.ahmadmouslimani.transaction.repository.TransactionRepository;
import com.ahmadmouslimani.transaction.service.TransactionRejectionService;
import com.ahmadmouslimani.transaction.service.client.AccountFeignClient;
import com.ahmadmouslimani.transaction.service.client.CardFeignClient;
import com.ahmadmouslimani.transaction.service.client.FraudFeignClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TransactionRejectionService transactionRejectionService;
    @Mock
    private AccountFeignClient accountFeignClient;
    @Mock
    private CardFeignClient cardFeignClient;
    @Mock
    private FraudFeignClient fraudClient;
    @Spy
    private TransactionMapper mapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private TransactionRequestDTO request;
    private CardResponseDTO cardResponse;
    private AccountResponseDTO accountResponse;
    private final UUID card_id = UUID.randomUUID();
    private final UUID account_id = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        request = new TransactionRequestDTO(new BigDecimal("100.00"), "D", "1234123412341234");

        cardResponse = new CardResponseDTO(
                card_id,
                LocalDate.now().plusYears(1),
                "1234123412341234",
                "ACTIVE",
                account_id
        );

        accountResponse = new AccountResponseDTO(
                account_id,
                "ACTIVE",
                new BigDecimal("500.00")
        );
    }

    @Test
    @DisplayName("Create Transaction - Success Approved")
    void createTransaction_Success() {
        //Arrange
        when(cardFeignClient.getCardByCardNumber(anyString())).thenReturn(cardResponse);
        when(accountFeignClient.getAccountById(account_id)).thenReturn(accountResponse);
        when(fraudClient.checkFraud(any())).thenReturn(new FraudResponseDTO(false, null));

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(UUID.randomUUID());
        savedTransaction.setStatus(TransactionStatus.APPROVED);
        savedTransaction.setTransactionAmount(new BigDecimal("100.00"));

        when(transactionRepository.save(any())).thenReturn(savedTransaction);

        //Act
        TransactionResponseDTO result = transactionService.createTransaction(request);

        //Assert
        assertNotNull(result.id());
        assertEquals(TransactionStatus.APPROVED, result.status());
        assertEquals(0, result.transactionAmount().compareTo(new BigDecimal("100.00")));

        verify(accountFeignClient).adjustBalance(eq(account_id), any(), eq("D"));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Create Transaction - Rejected due to Fraud (Exceed Amount Limit)")
    void createTransaction_Fraudulent_ExceededAmountLimit() {
        //Arrange
        when(cardFeignClient.getCardByCardNumber(anyString())).thenReturn(cardResponse);
        when(accountFeignClient.getAccountById(account_id)).thenReturn(accountResponse);

        String reason = "Transaction amount exceed";
        when(fraudClient.checkFraud(any())).thenReturn(new FraudResponseDTO(true, reason));

        Transaction rejectedTransaction = new Transaction();
        rejectedTransaction.setId(UUID.randomUUID());
        rejectedTransaction.setStatus(TransactionStatus.REJECTED);

        when(transactionRepository.save(any())).thenReturn(rejectedTransaction);
        when(transactionRepository.findById(any())).thenReturn(Optional.of(rejectedTransaction));

        //Act
        TransactionResponseDTO result = transactionService.createTransaction(request);

        //Assert
        assertTrue(result.isFraudulent(), "Transaction should be marked as fraudulent");
        assertEquals(reason, result.details());

        verify(accountFeignClient, never()).adjustBalance(any(), any(), any());
    }

    @Test
    @DisplayName("Create Transaction - Rejected due to Fraud (Exceed Frequency Limit)")
    void createTransaction_Fraudulent_ExceededFrequencyLimit() {
        //Arrange
        when(cardFeignClient.getCardByCardNumber(anyString())).thenReturn(cardResponse);
        when(accountFeignClient.getAccountById(account_id)).thenReturn(accountResponse);
        String reason = "Frequency limit exceeded";
        when(fraudClient.checkFraud(any())).thenReturn(new FraudResponseDTO(true, reason));

        Transaction rejectedTransaction = new Transaction();
        rejectedTransaction.setId(UUID.randomUUID());
        rejectedTransaction.setStatus(TransactionStatus.REJECTED);


        when(transactionRepository.save(any())).thenReturn(rejectedTransaction);
        when(transactionRepository.findById(any())).thenReturn(Optional.of(rejectedTransaction));

        //Act
        TransactionResponseDTO result = transactionService.createTransaction(request);

        //Assert
        assertTrue(result.isFraudulent(), "Transaction should be marked as fraudulent");
        assertEquals(reason, result.details());

        verify(accountFeignClient, never()).adjustBalance(any(), any(), any());
        verify(transactionRejectionService).createTransactionRejection(any());
    }

    @Test
    @DisplayName("Create Transaction - Rejected due to Inactive Card")
    void createTransaction_InactiveCard_ReturnsRejection() {
        //Arrange
        CardResponseDTO inactiveCard = new CardResponseDTO(card_id, LocalDate.now().plusYears(1), "1234123412341234", "INACTIVE", account_id);
        when(cardFeignClient.getCardByCardNumber(anyString())).thenReturn(inactiveCard);

        //Finalize Rejection mocks
        Transaction rejectedTransaction = new Transaction();
        rejectedTransaction.setId(UUID.randomUUID());
        when(transactionRepository.save(any())).thenReturn(rejectedTransaction);
        when(transactionRepository.findById(any())).thenReturn(Optional.of(rejectedTransaction));

        //Act
        TransactionResponseDTO result = transactionService.createTransaction(request);

        //Assert
        assertNotNull(result);
        //finalizeRejection uses the exception message as "details"
        assertTrue(result.details().contains("Card is INACTIVE"));
        verify(fraudClient, never()).checkFraud(any());
    }

    @Test
    @DisplayName("Create Transaction - Rejected due to Expired Card")
    void createTransaction_ExpiredCard_ReturnsRejection() {
        //Arrange: Set expiry to yesterday
        CardResponseDTO expiredCard = new CardResponseDTO(card_id, LocalDate.now().minusDays(1), "1234123412341234", "ACTIVE", account_id);
        when(cardFeignClient.getCardByCardNumber(anyString())).thenReturn(expiredCard);
        when(accountFeignClient.getAccountById(account_id)).thenReturn(accountResponse);

        Transaction rejectedTransaction = new Transaction();
        rejectedTransaction.setId(UUID.randomUUID());
        when(transactionRepository.save(any())).thenReturn(rejectedTransaction);
        when(transactionRepository.findById(any())).thenReturn(Optional.of(rejectedTransaction));

        //Act
        TransactionResponseDTO result = transactionService.createTransaction(request);

        //Assert
        assertTrue(result.details().contains("expired"));
        verify(fraudClient, never()).checkFraud(any());
    }

    @Test
    @DisplayName("Create Transaction - Rejected due to Inactive Account")
    void createTransaction_InactiveAccount_ReturnsRejection() {
        //Arrange
        AccountResponseDTO inactiveAccount = new AccountResponseDTO(account_id, "INACTIVE", new BigDecimal("500.00"));
        when(cardFeignClient.getCardByCardNumber(anyString())).thenReturn(cardResponse);
        when(accountFeignClient.getAccountById(account_id)).thenReturn(inactiveAccount);

        Transaction rejectedTransaction = new Transaction();
        rejectedTransaction.setId(UUID.randomUUID());
        when(transactionRepository.save(any())).thenReturn(rejectedTransaction);
        when(transactionRepository.findById(any())).thenReturn(Optional.of(rejectedTransaction));

        //Act
        TransactionResponseDTO result = transactionService.createTransaction(request);

        //Assert
        assertTrue(result.details().contains("Account is INACTIVE"));
        verify(fraudClient, never()).checkFraud(any());
    }

    @Test
    @DisplayName("Create Transaction - Rejected due to Insufficient Balance")
    void createTransaction_InsufficientBalance_ReturnsRejection() {
        //Arrange: Request Debit 1000 while balance is 500
        TransactionRequestDTO highAmountRequest = new TransactionRequestDTO(new BigDecimal("1000.00"), "D", "1234123412341234");
        when(cardFeignClient.getCardByCardNumber(anyString())).thenReturn(cardResponse);
        when(accountFeignClient.getAccountById(account_id)).thenReturn(accountResponse);

        Transaction rejectedTransaction = new Transaction();
        rejectedTransaction.setId(UUID.randomUUID());
        when(transactionRepository.save(any())).thenReturn(rejectedTransaction);
        when(transactionRepository.findById(any())).thenReturn(Optional.of(rejectedTransaction));

        //Act
        TransactionResponseDTO result = transactionService.createTransaction(highAmountRequest);

        //Assert
        assertTrue(result.details().contains("insufficient"));
        verify(fraudClient, never()).checkFraud(any());
    }
}