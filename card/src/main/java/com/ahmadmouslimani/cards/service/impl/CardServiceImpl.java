package com.ahmadmouslimani.cards.service.impl;

import com.ahmadmouslimani.cards.dto.CardRequestDTO;
import com.ahmadmouslimani.cards.dto.CardResponseDTO;
import com.ahmadmouslimani.cards.encryption.HashUtils;
import com.ahmadmouslimani.cards.entity.Card;
import com.ahmadmouslimani.cards.enums.CardStatus;
import com.ahmadmouslimani.cards.exception.CardAlreadyExistsException;
import com.ahmadmouslimani.cards.exception.ResourceNotFoundException;
import com.ahmadmouslimani.cards.mapper.CardMapper;
import com.ahmadmouslimani.cards.repository.CardRepository;
import com.ahmadmouslimani.cards.service.CardService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final CardMapper mapper;
    private final HashUtils hashUtils;

    @Override
    public CardResponseDTO createCard(CardRequestDTO cardRequestDTO) {
        String cardNumberHash = hashUtils.generateSearchHash(cardRequestDTO.cardNumber());

        if (cardRepository.existsByCardNumberHash(cardNumberHash)) {
            throw new CardAlreadyExistsException("Card already exists");
        }

        Card card = mapper.mapToEntity(cardRequestDTO);
        Card savedCard = cardRepository.save(card);
        return mapper.mapToDto(savedCard);
    }

    @Override
    public CardResponseDTO getCardById(UUID id) {
        Card card = cardRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Card", "Id", id.toString())
        );
        return mapper.mapToDto(card);
    }

    @Override
    public CardResponseDTO getCardByCardNumber(String cardNumber) {
        Card card = findCardByNumber(cardNumber);
        return mapper.mapToDto(card);
    }

    @Override
    public CardResponseDTO activateCard(String cardNumber) {
        Card card = findCardByNumber(cardNumber);
        card.setStatus(CardStatus.ACTIVE.name());
        return mapper.mapToDto(cardRepository.save(card));
    }

    @Override
    public CardResponseDTO deactivateCard(String cardNumber) {
        Card card = findCardByNumber(cardNumber);
        card.setStatus(CardStatus.INACTIVE.name());
        return mapper.mapToDto(cardRepository.save(card));
    }

    private Card findCardByNumber(String cardNumber) {
        String searchHash = hashUtils.generateSearchHash(cardNumber);
        return cardRepository.findByCardNumberHash(searchHash).orElseThrow(
                () -> new ResourceNotFoundException("Card", "cardNumber", cardNumber)
        );
    }
}
