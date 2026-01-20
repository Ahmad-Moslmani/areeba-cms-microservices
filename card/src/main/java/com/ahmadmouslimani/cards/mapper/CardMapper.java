package com.ahmadmouslimani.cards.mapper;

import com.ahmadmouslimani.cards.dto.CardRequestDTO;
import com.ahmadmouslimani.cards.dto.CardResponseDTO;
import com.ahmadmouslimani.cards.entity.Card;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {

    public Card mapToEntity(CardRequestDTO dto) {
        if (dto == null) return null;

        Card card = new Card();
        card.setStatus(dto.status());
        card.setExpiry(dto.expiry());
        card.setCardNumber(dto.cardNumber());
        card.setAccountId(dto.accountId());

        return card;
    }

    public CardResponseDTO mapToDto(Card card) {
        if (card == null) return null;
        return new CardResponseDTO(
                card.getId(),
                card.getExpiry(),
                card.getCardNumber(),
                card.getStatus(),
                card.getAccountId()
        );
    }
}
