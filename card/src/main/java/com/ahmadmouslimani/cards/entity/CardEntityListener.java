package com.ahmadmouslimani.cards.entity;

import com.ahmadmouslimani.cards.encryption.HashUtils;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardEntityListener {

    private final HashUtils hashUtils;

    @PrePersist
    @PreUpdate
    public void handleBeforeSave(Card card) {
        if (card.getCardNumber() != null) {
            String hash = hashUtils.generateSearchHash(card.getCardNumber());
            card.setCardNumberHash(hash);
        }
    }
}