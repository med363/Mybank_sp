package com.example.Mybank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

/**
 * Security Configuration for the Bank Application.
 * 
 * By default, Spring Security protects all endpoints and requires authentication.
 * Since we are currently testing the banking logic (creating accounts, transfers, etc.)
 * via generic REST clients (like Postman or Curl) without a frontend or token provider,
 * we need to explicitly configure security to allow access.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF (Cross-Site Request Forgery) protection.
            // CSRF is important for browser-based session applications, but for stateless REST APIs
            // or simple testing with Postman/Curl, it often gets in the way and causes 403/401 errors.
            .csrf(AbstractHttpConfigurer::disable)
            
            // Configure authorization rules
            .authorizeHttpRequests(auth -> auth
                // Allow all requests to our API endpoints starting with /api/
                // This means 'create account', 'transfer', etc. can be called without logging in.
                .requestMatchers("/api/**").permitAll()
                
                // For any other request (if we add more), strictly require authentication.
                // This is a good practice: "deny by default, allow by exception".
                .anyRequest().authenticated()
            )
            
            // Disable default creating of session (stateless) - useful for REST APIs
             // However, for simple testing we can leave session management as default or configure it.
             // We'll keep it simple for now. 
            ;

        return http.build();
    }
}
