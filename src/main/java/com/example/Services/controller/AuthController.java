package com.example.Services.controller;

import com.example.Services.dto.LoginRequestDto;
import com.example.Services.jwtSecurity.JwtResponse;
import com.example.Services.jwtSecurity.JwtUtil;
import com.example.Services.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * Authenticates a user and returns a JWT token.
     * This endpoint:
     * 1. Validates the provided credentials
     * 2. Generates a JWT token if credentials are valid
     * 3. Returns the token to the client
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
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }
}
