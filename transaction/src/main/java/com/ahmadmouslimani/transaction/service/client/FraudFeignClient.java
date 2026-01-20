package com.ahmadmouslimani.transaction.service.client;

import com.ahmadmouslimani.transaction.dto.FraudRequestDTO;
import com.ahmadmouslimani.transaction.dto.FraudResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "fraud", url = "${application.config.fraud-service-url}")
public interface FraudFeignClient {

    @PostMapping("/api/fraud/check")
    FraudResponseDTO checkFraud(@RequestBody FraudRequestDTO request);
}