package com.ahmadmouslimani.fraud.service.impl;

import com.ahmadmouslimani.fraud.dto.FraudRequestDTO;
import com.ahmadmouslimani.fraud.dto.FraudResponseDTO;
import com.ahmadmouslimani.fraud.entity.FraudAuditLog;
import com.ahmadmouslimani.fraud.entity.FraudPolicy;
import com.ahmadmouslimani.fraud.repository.FraudAuditLogRepository;
import com.ahmadmouslimani.fraud.repository.FraudPolicyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class FraudAuditLogServiceImplTest {

    @Mock
    private FraudPolicyRepository policyRepository;
    @Mock
    private FraudAuditLogRepository auditRepository;

    @InjectMocks
    private FraudAuditLogServiceImpl fraudService;


    @Test
    @DisplayName("Should approve transaction when both amount and frequency are within the limits")
    void shouldApproveWhenWithinLimits() {
        //Arrange
        UUID cardId = UUID.randomUUID();
        FraudRequestDTO request = new FraudRequestDTO(cardId, new BigDecimal("500.00"));

        FraudPolicy policy = new FraudPolicy();
        policy.setFraudLimit(new BigDecimal("10000.00"));
        policy.setTimeInterval(Duration.ofHours(1));

        when(policyRepository.findAll()).thenReturn(List.of(policy));
        when(auditRepository.countByCardIdAndCreatedAtAfter(eq(cardId), any())).thenReturn(5L); // Below 8

        //Act
        FraudResponseDTO response = fraudService.validateTransaction(request);

        //Assert
        assertFalse(response.isFraudulent());
        assertEquals("Approved", response.rejectionReason());
        //Ensure saving FraudAuditLog, and the database wasn't hit multiple times unnecessarily
        verify(auditRepository, times(1)).save(any(FraudAuditLog.class));
    }


    @Test
    @DisplayName("Should reject transaction when amount exceeds the policy limit")
    void shouldRejectWhenAmountExceedsLimit() {
        //Arrange
        UUID cardId = UUID.randomUUID();
        BigDecimal limit = new BigDecimal("10000.00");
        BigDecimal excessiveAmount = new BigDecimal("10000.01");

        FraudRequestDTO request = new FraudRequestDTO(cardId, excessiveAmount);

        FraudPolicy policy = new FraudPolicy();
        policy.setFraudLimit(limit);
        policy.setTimeInterval(Duration.ofHours(1));

        when(policyRepository.findAll()).thenReturn(List.of(policy));

        //Act
        FraudResponseDTO response = fraudService.validateTransaction(request);

        //Assert
        assertTrue(response.isFraudulent(), "Transaction should be marked as fraudulent");
        assertTrue(response.rejectionReason().contains("exceeds"));
        assertTrue(response.rejectionReason().contains(limit.toString()));
        //Ensure saving FraudAuditLog, and the database wasn't hit multiple times unnecessarily
        verify(auditRepository, times(1)).save(any(FraudAuditLog.class));
    }


    @Test
    void shouldRejectWhenCardIdFrequencyExceeded() {
        //Arrange
        UUID cardId = UUID.randomUUID();
        FraudRequestDTO request = new FraudRequestDTO(cardId, new BigDecimal("100.00"));
        
        FraudPolicy policy = new FraudPolicy();
        policy.setFraudLimit(new BigDecimal("10000.00"));
        policy.setTimeInterval(Duration.ofHours(1));

        when(policyRepository.findAll()).thenReturn(List.of(policy));
        //Mock that 8 transactions already exist
        when(auditRepository.countByCardIdAndCreatedAtAfter(eq(cardId), any())).thenReturn(8L);

        //Act
        FraudResponseDTO response = fraudService.validateTransaction(request);

        //Assert
        assertTrue(response.isFraudulent());
        assertEquals("Frequency limit exceeded: more than 8 transactions in 1 hour", response.rejectionReason());
        //Ensure saving FraudAuditLog, and the database wasn't hit multiple times unnecessarily
        verify(auditRepository, times(1)).save(any(FraudAuditLog.class));
    }

    @Test
    @DisplayName("Should throw IllegalStateException if no policy exists")
    void shouldThrowExceptionWhenNoPolicyFound() {
        when(policyRepository.findAll()).thenReturn(List.of());

        assertThrows(IllegalStateException.class, () ->
                fraudService.validateTransaction(new FraudRequestDTO(UUID.randomUUID(), new BigDecimal("10.00")))
        );
    }

}