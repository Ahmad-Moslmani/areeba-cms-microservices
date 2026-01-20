package com.ahmadmouslimani.fraud.repository;

import com.ahmadmouslimani.fraud.entity.FraudAuditLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
public class FraudRepositoryIntegrationTest {
    @Autowired
    private FraudAuditLogRepository auditRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should count transactions by cardId within the sliding time window")
    void shouldCountTransactionsByCardId() {
        //Arrange
        UUID cardId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        //Transaction from 90 minutes ago (should be excluded from 1h window)
        FraudAuditLog oldLog = new FraudAuditLog();
        oldLog.setCardId(cardId);
        entityManager.persist(oldLog);

        //Update the timestamp manually because @PrePersist sets it to 'now'
        entityManager.getEntityManager()
                .createNativeQuery("UPDATE fraud_audit_log SET created_at = :time WHERE id = :id")
                .setParameter("time", now.minusMinutes(90))
                .setParameter("id", oldLog.getId())
                .executeUpdate();

        //Create 3 Transactions from "now"
        for (int i = 0; i < 3; i++) {
            FraudAuditLog currentLog = new FraudAuditLog();
            currentLog.setCardId(cardId);
            auditRepository.save(currentLog);
        }

        auditRepository.flush();

        //Act
        LocalDateTime oneHourAgo = now.minusHours(1);
        long count = auditRepository.countByCardIdAndCreatedAtAfter(cardId, oneHourAgo);

        //Assert
        assertEquals(3, count, "Should only count the 3 recent transactions");
    }
}
