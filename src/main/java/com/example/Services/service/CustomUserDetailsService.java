package com.example.Services.service;

import com.example.Services.model.User;
import com.example.Services.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Custom implementation of Spring Security's UserDetailsService.
 * This service is responsible for loading user details during authentication.
 * It:
 * - Retrieves user information from the database
 * - Converts the user's role to Spring Security authorities
 * - Creates a UserDetails object for Spring Security
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

        // Repository for accessing user data
        private final UserRepository userRepository;

        /**
         * Loads a user by their username.
         * This method is called by Spring Security during authentication to:
         * 1. Find the user in the database
         * 2. Convert the user's role to a Spring Security authority
         * 3. Create a UserDetails object with the user's credentials and authorities
         *
         * @param username The username to load
         * @return UserDetails object containing the user's information
         * @throws UsernameNotFoundException if the user is not found
         */
        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                // Find user in database or throw exception if not found
                var user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

                // Convert user role to Spring Security authority
                // Format: "ROLE_" + role name (e.g., "ROLE_ADMIN" or "ROLE_CUSTOMER")
                List<SimpleGrantedAuthority> authorities = Collections
                                .singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

                // Create and return Spring Security UserDetails object
                return new org.springframework.security.core.userdetails.User(
                                user.getUsername(),
                                user.getPassword(),
                                authorities);
        }
}