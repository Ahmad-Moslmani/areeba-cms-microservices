package com.ahmadmouslimani.transaction.exception.account;

import com.ahmadmouslimani.transaction.exception.TransactionException;

public class InactiveAccountException extends TransactionException {
    public InactiveAccountException() {
        super("Transaction failed. Account is INACTIVE");
    }
}