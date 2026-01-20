package com.ahmadmouslimani.cards.service;

import com.ahmadmouslimani.cards.dto.CardRequestDTO;
import com.ahmadmouslimani.cards.dto.CardResponseDTO;

import java.util.UUID;

public interface CardService {

    CardResponseDTO createCard(CardRequestDTO request);

    CardResponseDTO getCardById(UUID id);

    CardResponseDTO getCardByCardNumber(String cardNumber);

    CardResponseDTO activateCard(String cardNumber);

    CardResponseDTO deactivateCard(String cardNumber);
}
