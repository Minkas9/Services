package com.example.Services.controller;

import com.example.Services.dto.LoginRequestDto;
import com.example.Services.jwtSecurity.JwtResponse;
import com.example.Services.jwtSecurity.JwtUtil;
import com.example.Services.model.User;
import com.example.Services.repository.UserRepository;
import com.example.Services.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller responsible for handling authentication-related requests.
 * This controller provides endpoints for user authentication and token
 * generation.
 * It uses JWT (JSON Web Token) for stateless authentication.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    // Authentication manager for validating user credentials
    private final AuthenticationManager authenticationManager;
    // Service for user-related operations
    private final UserService userService;
    // Utility for JWT token operations
    private final JwtUtil jwtUtil;
    // Repository for accessing user data
    private final UserRepository userRepository;

    /**
     * Authenticates a user and returns a JWT token.
     * This endpoint:
     * 1. Validates the provided credentials
     * 2. Checks if the user is banned
     * 3. Generates a JWT token if credentials are valid and user is not banned
     * 4. Returns the token to the client
     *
     * @param loginRequest DTO containing username and password
     * @return ResponseEntity with JWT token or error message
     */
    @Operation(summary = "Login user", description = "Authenticates user and returns JWT token")
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Parameter(description = "User credentials", required = true) @RequestBody LoginRequestDto loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getUsername());
        try {
            // Check if user is banned before authentication
            User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
            if (user != null && user.isBanned()) {
                log.warn("Banned user {} attempted to log in", loginRequest.getUsername());

                // Prepare error response
                Map<String, String> error = new HashMap<>();
                error.put("error", "Account Banned");
                error.put("message",
                        user.getBanReason() != null ? user.getBanReason() : "Your account has been banned");

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            // Authenticate the user with provided credentials
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            // Get user details from the authenticated principal
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            // Generate JWT token for the authenticated user
            String token = jwtUtil.generateToken(userDetails);
            log.info("Login successful for user: {}", loginRequest.getUsername());
            // Return the token in the response
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (Exception e) {
            // Log the authentication failure
            log.error("Login failed for user: {}", loginRequest.getUsername(), e);
            // Return unauthorized status with error message
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
}
