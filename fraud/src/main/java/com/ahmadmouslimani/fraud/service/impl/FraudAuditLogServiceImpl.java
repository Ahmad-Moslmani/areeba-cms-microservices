package com.ahmadmouslimani.fraud.service.impl;

import com.ahmadmouslimani.fraud.dto.FraudRequestDTO;
import com.ahmadmouslimani.fraud.dto.FraudResponseDTO;
import com.ahmadmouslimani.fraud.entity.FraudAuditLog;
import com.ahmadmouslimani.fraud.entity.FraudPolicy;
import com.ahmadmouslimani.fraud.enums.FraudRule;
import com.ahmadmouslimani.fraud.repository.FraudAuditLogRepository;
import com.ahmadmouslimani.fraud.repository.FraudPolicyRepository;
import com.ahmadmouslimani.fraud.service.FraudAuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FraudAuditLogServiceImpl implements FraudAuditLogService {
    private final FraudPolicyRepository policyRepository;
    private final FraudAuditLogRepository auditRepository;

    @Transactional
    public FraudResponseDTO validateTransaction(FraudRequestDTO request) {
        FraudPolicy policy = policyRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Fraud Policy not initialized"));

        FraudRule rule = determineFraudRule(request, policy);
        FraudResponseDTO response = switch (rule) {
            case EXCEED_AMOUNT_LIMIT -> FraudResponseDTO.rejected("Transaction amount exceeds $" + policy.getFraudLimit());
            case EXCEED_FREQUENCY_LIMIT -> FraudResponseDTO.rejected("Frequency limit exceeded: more than 8 transactions in 1 hour");
            default -> FraudResponseDTO.approved();
        };

        saveAudit(request);
        return response;
    }

    private void saveAudit(FraudRequestDTO request) {
        FraudAuditLog fraudAuditLog = new FraudAuditLog();
        fraudAuditLog.setCardId(request.cardId());
        auditRepository.save(fraudAuditLog);
    }

    private FraudRule determineFraudRule(FraudRequestDTO request, FraudPolicy policy) {
        if (request.amount().compareTo(policy.getFraudLimit()) > 0) {
            return FraudRule.EXCEED_AMOUNT_LIMIT;
        }
        LocalDateTime window = LocalDateTime.now().minus(policy.getTimeInterval());
        long count = auditRepository.countByCardIdAndCreatedAtAfter(request.cardId(), window);
        if (count >= 8) {
            return FraudRule.EXCEED_FREQUENCY_LIMIT;
        }
        return FraudRule.NONE;
    }
}
