package com.ahmadmouslimani.cards.repository;

import com.ahmadmouslimani.cards.config.EncryptionConfig;
import com.ahmadmouslimani.cards.encryption.CardNumberEncryptorConverter;
import com.ahmadmouslimani.cards.encryption.HashUtils;
import com.ahmadmouslimani.cards.entity.Card;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@DataJpaTest
@Import({EncryptionConfig.class, CardNumberEncryptorConverter.class, HashUtils.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
class CardSecurityIntegrationTest {
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private HashUtils hashUtils;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldPersistAndRetrieveCardByHash() {
        //Arrange
        String cardNumber = "1234123412341234";

        Card card = new Card();
        card.setCardNumber(cardNumber);
        card.setExpiry(LocalDate.now().plusYears(2));
        card.setStatus("ACTIVE");
        card.setAccountId(UUID.randomUUID());

        //Act
        //the cardNumberHash should be automatically set in the Entity before persist to the database
        cardRepository.save(card);

        //Assert
        String lookUpHash = hashUtils.generateSearchHash(cardNumber);
        Optional<Card> foundCard = cardRepository.findByCardNumberHash(lookUpHash);

        assertTrue(foundCard.isPresent(), "The card should be found in the database using the hash");
        assertEquals(lookUpHash, foundCard.get().getCardNumberHash());
    }


    @Test
    @DisplayName("Should verify encryption is applied by bypassing the converter with a native query")
    void shouldVerifyEncryptionIsApplied() {
        //Arrange
        String rawCardNumber = "1234123412341234";
        Card card = new Card();
        card.setCardNumber(rawCardNumber);
        card.setExpiry(LocalDate.now().plusYears(2));
        card.setStatus("ACTIVE");
        card.setAccountId(UUID.randomUUID());

        //Act
        Card savedCard = cardRepository.saveAndFlush(card);

        //Assert: Verify JPA Decryption
        Card loadedCard = cardRepository.findById(savedCard.getId()).orElseThrow();
        assertEquals(rawCardNumber, loadedCard.getCardNumber(), "JPA should decrypt automatically");

        // 4. Assert: Verify database Encryption (Bypass the converter)
        // By using a Native Query returning a String, JPA won't apply the AttributeConverter
        String rawValueInDb = (String) entityManager.getEntityManager()
                .createNativeQuery("SELECT card_number FROM card WHERE id = :id")
                .setParameter("id", savedCard.getId())
                .getSingleResult();

        assertNotNull(rawValueInDb);
        assertNotEquals(rawCardNumber, rawValueInDb, "The value stored in the database should be encrypted");
        assertFalse(rawValueInDb.contains(rawCardNumber), "The raw card digits should not exist in the database string");

        System.out.println("Encrypted string in DB: " + rawValueInDb);
    }
}