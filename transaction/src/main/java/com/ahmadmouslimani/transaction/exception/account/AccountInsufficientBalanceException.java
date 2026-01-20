package com.ahmadmouslimani.transaction.exception.account;

import com.ahmadmouslimani.transaction.exception.TransactionException;

public class AccountInsufficientBalanceException extends TransactionException {
    public AccountInsufficientBalanceException() {
        super("Transaction failed. Account has insufficient funds.");
    }
}