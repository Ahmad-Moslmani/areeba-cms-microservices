package com.ahmadmouslimani.transaction.exception.account;

import com.ahmadmouslimani.transaction.exception.ResourceNotFoundException;

public class AccountNotFoundException extends ResourceNotFoundException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}