package com.ahmadmouslimani.cards.encryption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
@Component
public class HashUtils {
    private final String secretSalt;
    private static final String HMAC_ALGO = "HmacSHA256";

    public HashUtils(@Value("${app.security.blind-index-salt}") String secretSalt) {
        this.secretSalt = secretSalt;
    }

    public String generateSearchHash(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }
        try {
            SecretKeySpec secretKey = new SecretKeySpec(
                    secretSalt.getBytes(StandardCharsets.UTF_8),
                    HMAC_ALGO
            );
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(secretKey);
            byte[] hashBytes = mac.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Encryption error: Could not generate blind index", e);
        }
    }
}