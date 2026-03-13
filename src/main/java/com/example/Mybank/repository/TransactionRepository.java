package com.example.Mybank.repository;

import com.example.Mybank.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for managing Transaction entities.
 * Provides basic CRUD operations and custom query methods.
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Find all transactions associated with a specific account ID
    List<Transaction> findByAccountId(Long accountId);
}
