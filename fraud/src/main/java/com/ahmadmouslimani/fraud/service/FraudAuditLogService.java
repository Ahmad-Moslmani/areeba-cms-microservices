package com.ahmadmouslimani.fraud.service;

import com.ahmadmouslimani.fraud.dto.FraudRequestDTO;
import com.ahmadmouslimani.fraud.dto.FraudResponseDTO;

import java.math.BigDecimal;
import java.util.UUID;

public interface FraudAuditLogService {
    FraudResponseDTO validateTransaction(FraudRequestDTO request);
}
