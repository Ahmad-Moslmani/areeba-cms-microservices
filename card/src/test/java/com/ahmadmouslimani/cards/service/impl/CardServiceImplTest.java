package com.ahmadmouslimani.cards.service.impl;

import com.ahmadmouslimani.cards.dto.CardRequestDTO;
import com.ahmadmouslimani.cards.dto.CardResponseDTO;
import com.ahmadmouslimani.cards.encryption.HashUtils;
import com.ahmadmouslimani.cards.entity.Card;
import com.ahmadmouslimani.cards.exception.CardAlreadyExistsException;
import com.ahmadmouslimani.cards.exception.ResourceNotFoundException;
import com.ahmadmouslimani.cards.mapper.CardMapper;
import com.ahmadmouslimani.cards.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
public class CardServiceImplTest {
    @Mock
    private CardRepository cardRepository;
    @Mock
    private HashUtils hashUtils;
    @Spy
    private CardMapper mapper;
    @InjectMocks
    private CardServiceImpl cardService;
    private CardRequestDTO requestDTO;
    private final String cardNumber = "1234123412341234";
    private final String cardHash = "mockedHash";
    private final UUID fixedId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        UUID accountId = UUID.randomUUID();
        LocalDate expiry = LocalDate.now().plusYears(1);
        String status = "ACTIVE";

        requestDTO = new CardRequestDTO(cardNumber, expiry, status, accountId);
    }


    @Test
    @DisplayName("Should create card successfully using real mapper logic")
    void createCard_Success() {
        //Arrange
        when(hashUtils.generateSearchHash(cardNumber)).thenReturn(cardHash);
        when(cardRepository.existsByCardNumberHash(cardHash)).thenReturn(false);

        //stub only the repository. The mapper will run its real mapping
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> {
            Card savedCard = inv.getArgument(0);
            savedCard.setId(fixedId);//Simulate database assigning ID
            return savedCard;
        });

        //Act
        CardResponseDTO result = cardService.createCard(requestDTO);

        //Assert
        assertNotNull(result.id());
        assertEquals(fixedId, result.id());
        assertEquals(cardNumber, result.cardNumber());
        assertEquals("ACTIVE", result.status());

        //Verify mapper was actually used
        verify(mapper).mapToEntity(any(CardRequestDTO.class));
        verify(mapper).mapToDto(any(Card.class));
        //ensure that the database wasn't hit multiple times unnecessarily
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    @DisplayName("Should throw CardAlreadyExistsException, when create new card and cardNumber already exist")
    void createCard_WhenAlreadyExistsThrowCardAlreadyExistsException() {
        when(hashUtils.generateSearchHash(cardNumber)).thenReturn(cardHash);
        when(cardRepository.existsByCardNumberHash(cardHash)).thenReturn(true);

        assertThrows(CardAlreadyExistsException.class, () -> cardService.createCard(requestDTO));
    }


    @Test
    @DisplayName("Should throw ResourceNotFoundException, when card not found for a card Id")
    void shouldThrowExceptionWhenCardNotFound_ForCardId() {
        UUID id = UUID.randomUUID();
        when(cardRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> cardService.getCardById(id));

        //the custom ResourceNotFoundException should include the card id
        assertTrue(exception.getMessage().contains(id.toString()));
        //ensure that the database wasn't hit multiple times unnecessarily
        verify(cardRepository, times(1)).findById(id);
    }


    @Test
    @DisplayName("Should throw ResourceNotFoundException, when card not found for a card number")
    void shouldThrowExceptionWhenCardNotFound_ForCardNumber() {
        when(hashUtils.generateSearchHash(cardNumber)).thenReturn(cardHash);
        when(cardRepository.findByCardNumberHash(cardHash)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> cardService.getCardByCardNumber(cardNumber));

        // the custom ResourceNotFoundException should include the card Number
        assertTrue(exception.getMessage().contains(cardNumber));
        // ensure that the database wasn't hit multiple times unnecessarily
        verify(cardRepository, times(1)).findByCardNumberHash(cardHash);
    }


    @Test
    @DisplayName("Should activate card and verify state change")
    void activateCard_Success() {
        //Arrange
        Card card = new Card();
        card.setCardNumber(cardNumber);
        card.setStatus("INACTIVE");

        when(hashUtils.generateSearchHash(cardNumber)).thenReturn(cardHash);
        when(cardRepository.findByCardNumberHash(cardHash)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        //Act
        CardResponseDTO result = cardService.activateCard(cardNumber);

        //Assert
        assertEquals("ACTIVE", result.status());
        //ensure that the database wasn't hit multiple times unnecessarily
        verify(cardRepository, times(1)).save(card);
    }

    @Test
    @DisplayName("Should deactivate card and verify state change")
    void deactivateCard_Success() {
        //Arrange
        Card card = new Card();
        card.setCardNumber(cardNumber);
        card.setStatus("ACTIVE");

        when(hashUtils.generateSearchHash(cardNumber)).thenReturn(cardHash);
        when(cardRepository.findByCardNumberHash(cardHash)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        //Act
        CardResponseDTO result = cardService.deactivateCard(cardNumber);

        //Assert
        assertEquals("INACTIVE", result.status());
        //ensure that the database wasn't hit multiple times unnecessarily
        verify(cardRepository, times(1)).save(card);
    }
}
