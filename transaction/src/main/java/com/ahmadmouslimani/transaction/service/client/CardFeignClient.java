package com.ahmadmouslimani.transaction.service.client;

import com.ahmadmouslimani.transaction.dto.CardResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="card", url = "${application.config.card-service-url}")
public interface CardFeignClient {
    @GetMapping("/api/card/fetch")
    CardResponseDTO getCardByCardNumber(@RequestParam("cardNumber") String cardNumber);
}
