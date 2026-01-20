package com.ahmadmouslimani.account.repository;

import com.ahmadmouslimani.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;
@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    @Modifying
    @Query("UPDATE Account a SET a.balance = (a.balance - :amount) WHERE a.id = :id AND a.balance >= :amount")
    int debitBalance(UUID id, BigDecimal amount);

    @Modifying
    @Query("UPDATE Account a SET a.balance = (a.balance + :amount) WHERE a.id = :id")
    int creditBalance(UUID id, BigDecimal amount);
}
