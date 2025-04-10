package com.example.Services.jwtSecurity;

import com.example.Services.enums.Role;
import com.example.Services.model.User;
import com.example.Services.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Component responsible for initializing the database with default users.
 * This class runs after the application context is fully initialized.
 * 
 * It creates three default users if they don't already exist:
 * 1. An admin user with ADMIN role
 * 2. A regular user with CUSTOMER role
 * 3. A banned user with CUSTOMER role and ban reason
 * 
 * This ensures the application has at least one user of each type for testing
 * and demonstration.
 */
@Component
@RequiredArgsConstructor
public class DataLoader {

    // Repository for user data persistence
    private final UserRepository userRepository;
    // Encoder for secure password storage
    private final PasswordEncoder passwordEncoder;

    /**
     * Initializes the database with default users.
     * This method is automatically called after the application context is fully
     * initialized.
     * 
     * For each default user:
     * 1. Checks if the user already exists
     * 2. If not, creates the user with appropriate role and credentials
     * 3. Encodes the password before saving
     * 
     * Default users created:
     * - admin/admin (ADMIN role)
     * - user/user (CUSTOMER role)
     * - banned/banned (CUSTOMER role, banned with reason)
     */
    @PostConstruct
    public void loadData() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin"))
                    .role(Role.ADMIN)
                    .banned(false)
                    .build());
        }

        if (userRepository.findByUsername("user").isEmpty()) {
            userRepository.save(User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("user"))
                    .role(Role.CUSTOMER)
                    .banned(false)
                    .build());
        }

        if (userRepository.findByUsername("banned").isEmpty()) {
            userRepository.save(User.builder()
                    .username("banned")
                    .password(passwordEncoder.encode("banned"))
                    .role(Role.CUSTOMER)
                    .banned(true)
                    .banReason("Your account is banned for violation of terms of service")
                    .build());
        }
    }
}