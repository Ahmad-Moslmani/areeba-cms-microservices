package com.ahmadmouslimani.fraud.repository;

import com.ahmadmouslimani.fraud.entity.FraudAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface FraudAuditLogRepository extends JpaRepository<FraudAuditLog, UUID> {

     //query is optimized by the index (cardId, createdAt)
    long countByCardIdAndCreatedAtAfter(UUID cardId, LocalDateTime timeThreshold);
}