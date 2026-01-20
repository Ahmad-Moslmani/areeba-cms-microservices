package com.ahmadmouslimani.transaction.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TransactionRejection {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "transaction_id", unique = true, nullable = false)
    @JsonIgnore
    private Transaction transaction;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private boolean isFraudulent;
}