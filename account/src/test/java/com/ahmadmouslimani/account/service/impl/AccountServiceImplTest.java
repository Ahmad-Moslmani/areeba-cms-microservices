package com.ahmadmouslimani.account.service.impl;

import com.ahmadmouslimani.account.dto.AccountRequestDTO;
import com.ahmadmouslimani.account.dto.AccountResponseDTO;
import com.ahmadmouslimani.account.entity.Account;
import com.ahmadmouslimani.account.exception.BusinessException;
import com.ahmadmouslimani.account.exception.ResourceNotFoundException;
import com.ahmadmouslimani.account.mapper.AccountMapper;
import com.ahmadmouslimani.account.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;
    @Spy
    private AccountMapper mapper;
    @InjectMocks
    private AccountServiceImpl accountService;
    private AccountRequestDTO requestDTO;
    private final UUID fixedId = UUID.randomUUID();


    @BeforeEach
    void setUp() {
        String status = "ACTIVE";
        BigDecimal balance = new BigDecimal("100.00");

        requestDTO = new AccountRequestDTO(status, balance);
    }


    @Test
    @DisplayName("Should create account successfully using real mapper logic")
    void createAccount_Success() {
        //Arrange
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> {
            Account savedAccount = inv.getArgument(0);
            savedAccount.setId(fixedId);
            return savedAccount;
        });

        //Act
        AccountResponseDTO result = accountService.createAccount(requestDTO);

        //Assert
        assertNotNull(result.id());
        assertEquals(requestDTO.status(), result.status());
        assertEquals(requestDTO.balance(), result.balance());

        //Verify mapper was actually used
        verify(mapper).mapToEntity(requestDTO);
        verify(mapper).mapToDto(any(Account.class));
        //ensure that the database wasn't hit multiple times unnecessarily
        verify(accountRepository, times(1)).save(any(Account.class));
    }


    @Test
    @DisplayName("Should throw ResourceNotFoundException when account does not exist")
    void shouldThrowExceptionWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.getAccountById(id));
    }

    @Test
    @DisplayName("Should throw BusinessException on insufficient funds during debit")
    void shouldThrowExceptionOnInsufficientFunds() {
        UUID id = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("500.00");
        
        // Mock debitBalance returning 0 (either not found or insufficient funds)
        when(accountRepository.debitBalance(eq(id), eq(amount))).thenReturn(0);

        assertThrows(BusinessException.class, () ->
            accountService.adjustBalance(id, amount, "D")
        );
    }


    @Test
    @DisplayName("Should update account successfully")
    void updateAccount_Success() {
        //Arrange
        Account account = new Account();
        account.setId(fixedId);
        account.setStatus("INACTIVE");
        account.setBalance(new BigDecimal("100.00"));

        AccountRequestDTO updateRequest = new AccountRequestDTO("ACTIVE", new BigDecimal("500.00"));

        when(accountRepository.findById(fixedId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        //Act
        AccountResponseDTO result = accountService.updateAccount(fixedId, updateRequest);

        //Assert
        assertNotNull(result);
        assertEquals(updateRequest.status(), result.status());
        assertEquals(updateRequest.balance(), result.balance());

        verify(accountRepository, times(1)).findById(fixedId);
        verify(accountRepository, times(1)).save(account);

        assertEquals("ACTIVE", account.getStatus());
        assertEquals(new BigDecimal("500.00"), account.getBalance());
    }

    @Test
    @DisplayName("Should credit balance successfully and return updated value")
    void adjustBalance_Credit_Success() {
        //Arrange
        BigDecimal initialBalance = new BigDecimal("200.00");
        BigDecimal creditAmount = new BigDecimal("100.00");
        BigDecimal expectedBalance = initialBalance.add(creditAmount);

        Account account = new Account();
        account.setId(fixedId);
        account.setBalance(initialBalance);

        //Mock the database update success (1 row affected)
        when(accountRepository.creditBalance(fixedId, creditAmount)).thenReturn(1);

        when(accountRepository.findById(fixedId)).thenAnswer(inv -> {
            account.setBalance(expectedBalance);
            return Optional.of(account);
        });

        //Act
        AccountResponseDTO result = accountService.adjustBalance(fixedId, creditAmount, "C");

        //Assert
        assertNotNull(result);
        assertEquals(expectedBalance, result.balance());
        verify(accountRepository, times(1)).findById(fixedId);
        verify(accountRepository, times(1)).creditBalance(fixedId, creditAmount);
        verify(accountRepository, never()).debitBalance(any(), any());
    }

    @Test
    @DisplayName("Should throw BusinessException when credit fails (Account Not Found)")
    void adjustBalance_Credit_Failed() {
        //Arrange
        BigDecimal creditAmount = new BigDecimal("100.00");

        //Mock the repository return 0 because the ID does not exist
        when(accountRepository.creditBalance(fixedId, creditAmount)).thenReturn(0);

        //Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () ->
                accountService.adjustBalance(fixedId, creditAmount, "C")
        );

        assertTrue(exception.getMessage().contains("not found"));
        verify(accountRepository, times(1)).creditBalance(fixedId, creditAmount);
        verify(accountRepository, never()).debitBalance(any(), any());
    }


    @Test
    @DisplayName("Should debit balance successfully when funds are sufficient")
    void adjustBalance_Debit_Success() {
        //Arrange
        BigDecimal initialBalance = new BigDecimal("1000.00");
        BigDecimal debitAmount = new BigDecimal("400.00");
        BigDecimal expectedBalance = new BigDecimal("600.00");

        Account account = new Account();
        account.setId(fixedId);
        account.setBalance(initialBalance);

        //Mock successful update: 1 row modified because (1000 >= 400)
        when(accountRepository.debitBalance(fixedId, debitAmount)).thenReturn(1);

        //Simulate the database fetch after the update
        when(accountRepository.findById(fixedId)).thenAnswer(inv -> {
            account.setBalance(expectedBalance);
            return Optional.of(account);
        });

        //Act
        AccountResponseDTO result = accountService.adjustBalance(fixedId, debitAmount, "D");

        //Assert
        assertNotNull(result);
        assertEquals(expectedBalance, result.balance());
        verify(accountRepository, times(1)).debitBalance(fixedId, debitAmount);
        verify(accountRepository, never()).creditBalance(any(), any());
    }

    @Test
    @DisplayName("Should throw BusinessException when balance is insufficient")
    void adjustBalance_Debit_InsufficientFunds() {
        //Arrange
        BigDecimal currentBalance = new BigDecimal("50.00");
        BigDecimal debitAmount = new BigDecimal("100.00");

        // The SQL query returns 0 because the WHERE condition (50 >= 100)
        when(accountRepository.debitBalance(fixedId, debitAmount)).thenReturn(0);

        //Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () ->
                accountService.adjustBalance(fixedId, debitAmount, "D")
        );

        assertTrue(exception.getMessage().contains("Insufficient funds"));
        verify(accountRepository, times(1)).debitBalance(fixedId, debitAmount);
        verify(accountRepository, never()).creditBalance(any(), any());

    }

    @Test
    @DisplayName("Should delete account successfully when it exists")
    void deleteAccount_Success() {
        //Arrange
        Account existingAccount = new Account();
        existingAccount.setId(fixedId);
        existingAccount.setStatus("ACTIVE");

        when(accountRepository.findById(fixedId)).thenReturn(Optional.of(existingAccount));

        //Act
        accountService.deleteAccount(fixedId);

        //Assert
        verify(accountRepository, times(1)).findById(fixedId);
        verify(accountRepository, times(1)).delete(existingAccount);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when try to delete account that does not exist")
    void deleteAccount_NotFound() {
        //Arrange
        when(accountRepository.findById(fixedId)).thenReturn(Optional.empty());

        //Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                accountService.deleteAccount(fixedId)
        );

        assertTrue(exception.getMessage().contains("not found"));
        verify(accountRepository, never()).delete(any(Account.class));
    }
}