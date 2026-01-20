package com.ahmadmouslimani.cards.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

@Configuration
public class EncryptionConfig {
    private static final String ENCRYPTION_PASSWORD =
            System.getenv().getOrDefault("CARD_SECRET_KEY", "ABCDEFGHIJKLMNOPQRSTUVWXYZ");

    private static final String SALT = System.getenv().getOrDefault("CARD_ENCRYPTION_SALT", "5c0744940b5c369b"); //Hex


    @Bean
    public TextEncryptor textEncryptor() {
        return Encryptors.text(ENCRYPTION_PASSWORD, SALT);
    }
}
