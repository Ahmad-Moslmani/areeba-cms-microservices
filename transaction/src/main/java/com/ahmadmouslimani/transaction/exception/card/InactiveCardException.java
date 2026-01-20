package com.ahmadmouslimani.transaction.exception.card;

import com.ahmadmouslimani.transaction.exception.TransactionException;

public class InactiveCardException extends TransactionException {
    public InactiveCardException() {
        super("Transaction failed. Card is INACTIVE");
    }
}