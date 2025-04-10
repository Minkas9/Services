package com.example.Services.jwtSecurity;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;

/**
 * Configuration class for Spring Security authentication components.
 * This class provides beans for:
 * - Password encoding (BCrypt)
 * - Authentication manager setup
 * 
 * It configures the authentication provider to use the custom
 * UserDetailsService
 * and the BCryptPasswordEncoder for secure password handling.
 */
@Configuration
@RequiredArgsConstructor
public class AuthenticationConfig {

    // Custom UserDetailsService implementation for loading user details
    private final UserDetailsService userDetailsService;

    /**
     * Creates a BCryptPasswordEncoder bean for secure password hashing.
     * BCrypt is a strong one-way hashing function designed specifically for
     * passwords.
     * 
     * @return BCryptPasswordEncoder instance for password encoding
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures and creates the AuthenticationManager bean.
     * This bean is responsible for processing authentication requests.
     * 
     * The implementation:
     * 1. Creates a DaoAuthenticationProvider
     * 2. Sets the custom UserDetailsService for user lookup
     * 3. Sets the BCryptPasswordEncoder for password verification
     * 4. Wraps the provider in a ProviderManager
     * 
     * @param authConfig         Spring's AuthenticationConfiguration
     * @param userDetailsService Custom UserDetailsService implementation
     * @param passwordEncoder    BCryptPasswordEncoder for password verification
     * @return Configured AuthenticationManager
     * @throws Exception if configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig,
            UserDetailsService userDetailsService,
            BCryptPasswordEncoder passwordEncoder) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(Collections.singletonList(provider));
    }
}