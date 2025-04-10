package com.example.Services.service;

import com.example.Services.model.User;
import com.example.Services.repository.UserRepository;
import com.example.Services.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setRole(Role.CUSTOMER);
    }

    @Test
    void save_ShouldEncodePasswordAndSaveUser() {
        // Arrange
        String encodedPassword = "encodedPassword123";
        when(passwordEncoder.encode(eq("password123"))).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.save(testUser);

        // Assert
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(testUser);
        assertEquals(encodedPassword, testUser.getPassword());
    }

    @Test
    void existsByUsername_ShouldReturnTrue_WhenUsernameExists() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act
        boolean exists = userService.existsByUsername("testuser");

        // Assert
        assertTrue(exists);
        verify(userRepository).existsByUsername("testuser");
    }

    @Test
    void existsByUsername_ShouldReturnFalse_WhenUsernameDoesNotExist() {
        // Arrange
        when(userRepository.existsByUsername("nonexistent")).thenReturn(false);

        // Act
        boolean exists = userService.existsByUsername("nonexistent");

        // Assert
        assertFalse(exists);
        verify(userRepository).existsByUsername("nonexistent");
    }
}