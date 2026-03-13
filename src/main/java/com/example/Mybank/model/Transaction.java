package com.example.Mybank.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a bank transaction.
 * Stores transaction type, amount, date, and associated account.
 */
@Entity
@Table(name = "transactions") // Rename table to avoid SQL reserved keyword "TRANSACTION"
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_seq_gen")
    @SequenceGenerator(name = "transaction_seq_gen", sequenceName = "transactions_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount; // The transaction amount

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type; // Type of transaction: DEPOSIT, WITHDRAWAL, TRANSFER

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now(); // When the transaction occurred

    @ManyToOne // Many transactions can belong to one account
    @JoinColumn(name = "account_id", nullable = false)
    private Account account; // Relationship to the account
}
