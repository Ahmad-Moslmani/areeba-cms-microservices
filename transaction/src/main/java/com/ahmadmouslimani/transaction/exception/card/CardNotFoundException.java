package com.ahmadmouslimani.transaction.exception.card;

import com.ahmadmouslimani.transaction.exception.ResourceNotFoundException;

public class CardNotFoundException extends ResourceNotFoundException {
    public CardNotFoundException(String message) {
        super(message);
    }
}