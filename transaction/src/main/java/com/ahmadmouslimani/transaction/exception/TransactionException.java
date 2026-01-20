package com.ahmadmouslimani.transaction.exception;

public abstract class TransactionException extends RuntimeException {
    public TransactionException(String message) {
        super(message);
    }
}