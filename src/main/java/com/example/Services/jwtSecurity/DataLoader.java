package com.example.Services.jwtSecurity;

import com.example.Services.enums.Role;
import com.example.Services.model.User;
import com.example.Services.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
                    .banReason("Violation of terms of service")
                    .build());
        }
    }
}