package com.example.Services.controller;

import com.example.Services.model.User;
import com.example.Services.repository.UserRepository;
import com.example.Services.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for user management operations.
 * Provides endpoints for:
 * - Banning users
 * - Unbanning users
 * - Getting user information
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User management APIs")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    /**
     * Bans a user with the specified reason.
     * This endpoint is restricted to admin users only.
     *
     * @param username The username of the user to ban
     * @return ResponseEntity with success or error message
     */
    @Operation(summary = "Ban a user", description = "Bans a user with the specified reason")
    @PostMapping("/{username}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> banUser(
            @Parameter(description = "Username of the user to ban", required = true) @PathVariable String username) {

        log.info("Banning user: {}", username);

        return userRepository.findByUsername(username)
                .map(user -> {
                    user.setBanned(true);
                    user.setBanReason("Violation of terms of service");
                    userRepository.save(user);

                    Map<String, String> response = new HashMap<>();
                    response.put("message", "User " + username + " has been banned for violation of terms of service");

                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Unbans a user.
     * This endpoint is restricted to admin users only.
     *
     * @param username The username of the user to unban
     * @return ResponseEntity with success or error message
     */
    @Operation(summary = "Unban a user", description = "Unbans a previously banned user")
    @PostMapping("/{username}/unban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> unbanUser(
            @Parameter(description = "Username of the user to unban", required = true) @PathVariable String username) {

        log.info("Unbanning user: {}", username);

        return userRepository.findByUsername(username)
                .map(user -> {
                    user.setBanned(false);
                    user.setBanReason(null);
                    userRepository.save(user);

                    Map<String, String> response = new HashMap<>();
                    response.put("message", "User " + username + " has been unbanned");

                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Gets information about a user.
     * This endpoint is restricted to admin users only.
     *
     * @param username The username of the user to get information about
     * @return ResponseEntity with user information
     */
    @Operation(summary = "Get user information", description = "Gets information about a user")
    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserInfo(
            @Parameter(description = "Username of the user to get information about", required = true) @PathVariable String username) {

        log.info("Getting information for user: {}", username);

        return userRepository.findByUsername(username)
                .map(user -> {
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("username", user.getUsername());
                    userInfo.put("role", user.getRole());
                    userInfo.put("banned", user.isBanned());
                    userInfo.put("banReason", user.getBanReason());

                    return ResponseEntity.ok(userInfo);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}