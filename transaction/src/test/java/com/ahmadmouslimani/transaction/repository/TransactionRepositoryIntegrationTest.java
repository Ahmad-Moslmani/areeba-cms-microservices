package com.ahmadmouslimani.transaction.repository;

import com.ahmadmouslimani.transaction.entity.Transaction;
import com.ahmadmouslimani.transaction.entity.TransactionRejection;
import com.ahmadmouslimani.transaction.enums.TransactionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
class TransactionRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TransactionRejectionRepository rejectionRepository;

    @Test
    @DisplayName("Should persist Transaction and automatically generate Date and UUID")
    void shouldPersistTransaction() {
        //Arrange
        Transaction transaction = new Transaction();
        transaction.setTransactionAmount(new BigDecimal("100.00"));
        transaction.setTransactionType("D");
        transaction.setAccountId(UUID.randomUUID());
        transaction.setCardId(UUID.randomUUID());
        transaction.setStatus(TransactionStatus.APPROVED);

        //Act
        Transaction savedTransaction = transactionRepository.save(transaction);
        entityManager.flush(); //to triggers @PrePersist
        entityManager.clear();

        //Assert
        Transaction found = transactionRepository.findById(savedTransaction.getId()).orElseThrow();
        assertNotNull(found.getId(), "UUID should be generated");
        assertNotNull(found.getTransactionDate(), "TransactionDate should be set by @PrePersist");
        assertEquals(0, found.getTransactionAmount().compareTo(new BigDecimal("100.00")));
    }

    @Test
    @DisplayName("Should save Transaction with Rejection (OneToOne relationship)")
    void shouldSaveTransactionWithRejection() {
        //Arrange
        Transaction transaction = new Transaction();
        transaction.setTransactionAmount(new BigDecimal("100.00"));
        transaction.setTransactionType("D");
        transaction.setAccountId(UUID.randomUUID());
        transaction.setCardId(UUID.randomUUID());
        transaction.setStatus(TransactionStatus.REJECTED);


        //Act: Link Rejection to Transaction
        Transaction savedTransaction = transactionRepository.save(transaction);

        TransactionRejection rejection = new TransactionRejection();
        rejection.setTransaction(savedTransaction);
        rejection.setReason("Insufficient funds");
        rejection.setFraudulent(false);
        rejectionRepository.save(rejection);

        entityManager.flush(); //to triggers @PrePersist
        entityManager.clear();

        //Assert
        Transaction found = transactionRepository.findById(savedTransaction.getId()).orElseThrow();
        assertNotNull(found.getTransactionRejection());
        assertEquals("Insufficient funds", found.getTransactionRejection().getReason());
    }
}