package com.ahmadmouslimani.transaction.exception.card;

import com.ahmadmouslimani.transaction.exception.TransactionException;

import java.time.LocalDate;

public class ExpiredCardException extends TransactionException {
    public ExpiredCardException(LocalDate expiryDate) {
        super("Transaction failed. Card expired on " + expiryDate);
    }
}