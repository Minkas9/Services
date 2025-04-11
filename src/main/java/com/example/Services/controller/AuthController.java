package com.example.Services.controller;

import com.example.Services.dto.LoginRequestDto;
import com.example.Services.jwtSecurity.JwtResponse;
import com.example.Services.jwtSecurity.JwtUtil;
import com.example.Services.model.User;
import com.example.Services.repository.UserRepository;
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

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    // Authentication manager for validating user credentials
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Operation(summary = "Login user", description = "Authenticates user and returns JWT token")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Parameter(description = "User credentials", required = true) @RequestBody LoginRequestDto loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getUsername());
        try {
            // Patikriname ar vartotojas egzistuoja ir ar nėra užblokuotas
            User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
            if (user != null && user.isBanned()) {
                log.warn("Banned user {} attempted to log in", loginRequest.getUsername());

                Map<String, String> error = new HashMap<>();
                error.put("error", "Account Banned");
                error.put("message", user.getBanReason() != null ? user.getBanReason() : "Your account has been banned");

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            // Autentifikuojame naudotoją pagal username ir password
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            //Jei autentifikacija sėkminga, gauname naudotojo detales
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);
            log.info("Login successful for user: {}", loginRequest.getUsername());
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (Exception e) {
            log.error("Login failed for user: {}", loginRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
}
