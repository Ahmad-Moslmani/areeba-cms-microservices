package com.ahmadmouslimani.transaction.service.client;

import com.ahmadmouslimani.transaction.dto.AccountResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name="account", url = "${application.config.account-service-url}")
public interface AccountFeignClient {
    @GetMapping("/api/account/{id}")
    AccountResponseDTO getAccountById(@PathVariable("id") UUID accountId);

    @PatchMapping("/api/account/{id}/balance")
    AccountResponseDTO adjustBalance(
            @PathVariable("id") UUID id,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("transactionType") String transactionType
    );
}
