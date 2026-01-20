package com.ahmadmouslimani.transaction.entity;

import com.ahmadmouslimani.transaction.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal transactionAmount;

    @Column(nullable = false)
    private Instant transactionDate;

    @Column(nullable = false, length = 2)
    private String transactionType;

    @Column(updatable = false, nullable = false)
    private UUID accountId;

    @Column(updatable = false, nullable = false)
    private UUID cardId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @OneToOne(mappedBy = "transaction")
    private TransactionRejection transactionRejection;

    @PrePersist
    public void onCreate() {
        this.transactionDate = Instant.now();
    }
}
