package com.example.Mybank.controller;

import com.example.Mybank.model.User;
import com.example.Mybank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * Authentication Controller
 * Handles user registration and login.
 * This is where we verify credentials and create new users.
 */
@RestController // Marks this class as a REST controller
@RequestMapping("/api/auth") // Base URL for all endpoints in this controller
@RequiredArgsConstructor // Lombok: Auto-generates constructor for final fields
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registration Endpoint
     * Creates a new user with a hashed password.
     * 
     * @param payload Map containing "username" and "password"
     * @return The created User (without password) or an error message
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");

        // 1. Validation: Ensure both fields are provided
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("Username and password are required");
        }

        // 2. Check if user already exists
        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken");
        }

        // 3. Hash the password before saving! 
        // NEVER store plain text passwords.
        String hashedPassword = passwordEncoder.encode(password);

        // 4. Save the user
        User newUser = new User(username, hashedPassword);
        userRepository.save(newUser);

        return ResponseEntity.ok("User registered successfully");
    }

    /**
     * Login Endpoint
     * Verifies username and password.
     * 
     * @param payload Map containing "username" and "password"
     * @return User object on success, or 401 Unauthorized on failure
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");

        // 1. Find the user by username
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // 2. Check if the provided password matches the stored hash
            if (passwordEncoder.matches(password, user.getPassword())) {
                // Password is correct!
                // We return the user object but... 
                // IMPORTANT: We should verify the password field is not returned or is nulled out.
                // We don't want to send the hash to the frontend.
                user.setPassword(null); // Hide password hash in response
                return ResponseEntity.ok(user);
            }
        }

        // 3. If user not found OR password doesn't match
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }
}
