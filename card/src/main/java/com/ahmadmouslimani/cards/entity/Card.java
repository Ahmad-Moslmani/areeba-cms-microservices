package com.ahmadmouslimani.cards.entity;

import com.ahmadmouslimani.cards.encryption.CardNumberEncryptorConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@EntityListeners(CardEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false)
    private LocalDate expiry;

    @Column(nullable = false, length = 1000)
    @Convert(converter = CardNumberEncryptorConverter.class)
    private String cardNumber;

    @Column(nullable = false, unique = true, length = 255)
    private String cardNumberHash;

    @Column(nullable = false)
    private UUID accountId;

}
