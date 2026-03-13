package com.example.Mybank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration to enable Cross-Origin Resource Sharing (CORS).
 * This allows the React frontend (running on a different port, e.g., 3000)
 * to communicate with this Spring Boot backend (running on port 8080).
 */
@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Allow requests from any origin for development simplicity.
                // In production, you would restrict this to the specific domain of your frontend.
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000") // Explicitly allow React default port
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
