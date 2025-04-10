package com.example.Services.service;

import com.example.Services.model.User;
import com.example.Services.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class for managing User entities.
 * This class provides business logic for:
 * - Saving users with encoded passwords
 * - Checking if a username already exists
 * 
 * It ensures that passwords are properly encoded before storage
 * and provides methods for user-related operations.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    // Repository for user data persistence
    private final UserRepository userRepository;
    // Encoder for secure password storage
    private final PasswordEncoder passwordEncoder;

    /**
     * Saves a user to the database with an encoded password.
     * This method ensures that the user's password is properly encoded
     * using the configured PasswordEncoder before saving to the database.
     * 
     * @param user The user to save
     */
    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    /**
     * Checks if a user with the given username already exists.
     * This method is useful for validating new user registrations
     * to ensure username uniqueness.
     * 
     * @param username The username to check
     * @return true if a user with the username exists, false otherwise
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
