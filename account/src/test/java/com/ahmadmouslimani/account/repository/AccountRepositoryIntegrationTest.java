package com.ahmadmouslimani.account.repository;

import com.ahmadmouslimani.account.entity.Account;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
class AccountRepositoryIntegrationTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TestEntityManager entityManager;

    private UUID accountId;

    @BeforeEach
    void setUp() {
        Account account = new Account();
        account.setStatus("ACTIVE");
        account.setBalance(new BigDecimal("500.00"));
        Account savedAccount = entityManager.persistFlushFind(account);
        this.accountId = savedAccount.getId();
    }

    @Test
    @DisplayName("Should successfully credit balance and reflect in database")
    void shouldCreditBalance() {
        //Act
        BigDecimal creditAmount = new BigDecimal("100.00");
        int rowsAffected = accountRepository.creditBalance(accountId, creditAmount);
        entityManager.clear();

        //Assert
        Account updatedAccount = accountRepository.findById(accountId).orElseThrow();
        
        assertEquals(1, rowsAffected, "One row should be updated");
        assertEquals(0, updatedAccount.getBalance().compareTo(new BigDecimal("600.00")), 
            "Balance should be 500 + 100 = 600");
    }

    @Test
    @DisplayName("Should successfully debit balance when funds are sufficient")
    void shouldDebitBalanceWhenFundsSufficient() {
        //Act
        BigDecimal debitAmount = new BigDecimal("200.00");
        int rowsAffected = accountRepository.debitBalance(accountId, debitAmount);
        entityManager.clear();

        //Assert
        Account updatedAccount = accountRepository.findById(accountId).orElseThrow();
        
        assertEquals(1, rowsAffected, "One row should be updated");
        assertEquals(0, updatedAccount.getBalance().compareTo(new BigDecimal("300.00")), 
            "Balance should be 500 - 200 = 300");
    }

    @Test
    @DisplayName("Should fail to debit balance when funds are insufficient")
    void shouldNotDebitWhenFundsInsufficient() {
        BigDecimal excessiveAmount = new BigDecimal("600.00");
        int rowsAffected = accountRepository.debitBalance(accountId, excessiveAmount);
        entityManager.clear();

        Account updatedAccount = accountRepository.findById(accountId).orElseThrow();
        
        assertEquals(0, rowsAffected, "Zero rows should be updated due to balance constraint");
        assertEquals(0, updatedAccount.getBalance().compareTo(new BigDecimal("500.00")), 
            "Balance should remain 500 unchanged");
    }
}