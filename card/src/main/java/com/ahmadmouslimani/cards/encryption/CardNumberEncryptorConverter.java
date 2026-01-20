package com.ahmadmouslimani.cards.encryption;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

@Converter(autoApply = false)
public class CardNumberEncryptorConverter implements AttributeConverter<String, String> {
    private final TextEncryptor encryptor;

    @Autowired
    public CardNumberEncryptorConverter(TextEncryptor encryptor) {
        this.encryptor = encryptor;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null || encryptor == null) return null;
        return encryptor.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null || encryptor == null) return null;
        return encryptor.decrypt(dbData);
    }
}
