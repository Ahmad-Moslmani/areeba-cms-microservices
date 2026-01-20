package com.ahmadmouslimani.transaction.repository;

import com.ahmadmouslimani.transaction.entity.TransactionRejection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRejectionRepository extends JpaRepository<TransactionRejection, UUID> {
}
