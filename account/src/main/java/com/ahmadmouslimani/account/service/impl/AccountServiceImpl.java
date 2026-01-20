package com.ahmadmouslimani.account.service.impl;

import com.ahmadmouslimani.account.dto.AccountRequestDTO;
import com.ahmadmouslimani.account.dto.AccountResponseDTO;
import com.ahmadmouslimani.account.entity.Account;
import com.ahmadmouslimani.account.enums.TransactionType;
import com.ahmadmouslimani.account.exception.BusinessException;
import com.ahmadmouslimani.account.exception.ResourceNotFoundException;
import com.ahmadmouslimani.account.mapper.AccountMapper;
import com.ahmadmouslimani.account.repository.AccountRepository;
import com.ahmadmouslimani.account.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper mapper;

    @Override
    public AccountResponseDTO createAccount(AccountRequestDTO request) {
        Account account = mapper.mapToEntity(request);
        Account savedAccount = accountRepository.save(account);
        return mapper.mapToDto(savedAccount);
    }

    @Override
    public AccountResponseDTO getAccountById(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id.toString()));
        return mapper.mapToDto(account);
    }

    @Override
    @Transactional
    public AccountResponseDTO updateAccount(UUID id, AccountRequestDTO request) {
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id.toString()));


        existingAccount.setStatus(request.status());
        existingAccount.setBalance(request.balance());

        Account updatedAccount = accountRepository.save(existingAccount);
        return mapper.mapToDto(updatedAccount);
    }

    @Override
    @Transactional
    public AccountResponseDTO adjustBalance(UUID id, BigDecimal amount, String transactionType) {
        int updatedRows;
        if (TransactionType.D.name().equals(transactionType)) {
            updatedRows = accountRepository.debitBalance(id, amount);
            if (updatedRows == 0) throw new BusinessException("Insufficient funds or account not found");
        } else if (TransactionType.C.name().equals(transactionType)) {
            updatedRows = accountRepository.creditBalance(id, amount);
            if (updatedRows == 0) throw new BusinessException("account not found");
        }
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id.toString()));
        return mapper.mapToDto(account);
    }

    @Override
    @Transactional
    public void deleteAccount(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id.toString()));

        accountRepository.delete(account);
    }
}
