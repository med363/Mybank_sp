package com.example.Mybank.repository;

import com.example.Mybank.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * UserRepository
 * Provides basic CRUD operations for the User entity.
 * Spring Data JPA generates the implementation automatically at runtime.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Custom query method: Finds a user by their username.
    // Returns Optional to handle cases where the user does not exist.
    Optional<User> findByUsername(String username);
}
