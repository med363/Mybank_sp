package com.example.Mybank.repository;

import com.example.Mybank.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for managing Account entities.
 * Provides basic CRUD operations and custom query methods.
 */
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    // Find an account by its unique account number
    Optional<Account> findByAccountNumber(String accountNumber);

    // Find an account by IBAN
    Optional<Account> findByIban(String iban);

    // Find all accounts belonging to a specific user
    java.util.List<Account> findByUserId(Long userId);
}
