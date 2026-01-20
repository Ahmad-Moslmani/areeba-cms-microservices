package com.ahmadmouslimani.fraud.repository;

import com.ahmadmouslimani.fraud.entity.FraudPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FraudPolicyRepository extends JpaRepository<FraudPolicy, UUID> {
}