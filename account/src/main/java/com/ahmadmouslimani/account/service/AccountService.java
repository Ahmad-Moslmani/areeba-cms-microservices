package com.ahmadmouslimani.account.service;

import com.ahmadmouslimani.account.dto.AccountRequestDTO;
import com.ahmadmouslimani.account.dto.AccountResponseDTO;

import java.math.BigDecimal;
import java.util.UUID;

public interface AccountService {

    /**
     * Creates new account.
     * @param request The AccountRequestDTO
     * @return The created AccountResponseDTO
     */
    AccountResponseDTO createAccount(AccountRequestDTO request);

    /**
     * Get an account's details.
     * @param id The UUID of the account
     * @return The AccountResponseDTO
     */
    AccountResponseDTO getAccountById(UUID id);


    /**
     * Updates an existing account's status or balance.
     * @param id The UUID of the account to update
     * @param request The DTO containing updated values
     * @return The updated AccountResponseDTO
     */
    AccountResponseDTO updateAccount(UUID id, AccountRequestDTO request);


    /**
     * Adjusts Balance for an existing account.
     * @param id The UUID of the account to update
     * @param amount The amount to be added/deducted from the original amount
     * @param transactionType The type of the transaction. it can be "C" for Credit, or "D" for Debit
     * @return The AccountResponseDTO with the adjusted amount
     */
    AccountResponseDTO adjustBalance(UUID id, BigDecimal amount, String transactionType);

    /**
     * Deletes an account from the system.
     * @param id The UUID of the account to delete
     */
    void deleteAccount(UUID id);

}
