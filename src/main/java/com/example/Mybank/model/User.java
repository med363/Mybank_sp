package com.example.Mybank.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Entity
 * Represents a user in our banking system.
 * We use this to store login credentials.
 */
@Entity
@Table(name = "users") // Maps to the "users" table in the database
@Data // Lombok: Generates getters, setters, toString, equals, hashcode
@NoArgsConstructor // Lombok: Generates a no-args constructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Username must be unique to avoid duplicates
    @Column(unique = true, nullable = false)
    private String username;

    // We allow storing the password.
    // IMPORTANT: In a real app, this will be an ENCODED hash, not plain text.
    @Column(nullable = false)
    private String password;

    // Constructor for convenience
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
