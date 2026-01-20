package com.ahmadmouslimani.fraud.config;

import com.ahmadmouslimani.fraud.entity.FraudPolicy;
import com.ahmadmouslimani.fraud.repository.FraudPolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;

@Component
@Slf4j
@RequiredArgsConstructor
public class FraudPolicyInitializer implements CommandLineRunner {

    private final FraudPolicyRepository repository;

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            log.info("No Fraud Policy found. Initializing default system limits...");
            
            FraudPolicy defaultPolicy = new FraudPolicy();
            defaultPolicy.setFraudLimit(new BigDecimal("10000"));
            defaultPolicy.setTimeInterval(Duration.ofHours(1));
            
            repository.save(defaultPolicy);
            log.info("Default Fraud Policy created successfully.");
        } else {
            log.info("Fraud Policy already exists. Skipping initialization.");
        }
    }
}