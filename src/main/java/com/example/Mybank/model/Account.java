package com.example.Mybank.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a bank account.
 * Stores account details like owner, balance, IBAN, RIB.
 */
@Entity
@Table(name = "accounts") // Rename table to avoid reserved keywords
@Data // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor // Generates a no-args constructor
@AllArgsConstructor // Generates a constructor with all args
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq_gen")
    @SequenceGenerator(name = "account_seq_gen", sequenceName = "accounts_id_seq", allocationSize = 1)
    private Long id;

    @Column(unique = true, nullable = false)
    private String accountNumber; // Unique identifier for the account

    @Column(nullable = false)
    private String ownerName; // Name of the account holder

    @Column(nullable = false)
    private BigDecimal balance; // Current balance of the account

    private String bankCode = "10"; // Default Bank Code (Tunisian format example)
    private String branchCode = "001"; // Default Branch Code

    private String ribKey; // RIB Key (2 digits)
    
    @Column(unique = true)
    private String itemsRIB; // Concatenated RIB string for display

    @Column(unique = true)
    private String iban; // International Bank Account Number

    private LocalDateTime createdAt = LocalDateTime.now(); // Account creation timestamp
}
