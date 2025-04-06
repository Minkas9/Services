package com.example.Services.jwtSecurity;

import com.example.Services.model.User;
import com.example.Services.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class BannedUserFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Skip check for authentication endpoints
        if (request.getRequestURI().startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get the authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Skip check if not authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get the authenticated user
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();

            // Check if user is banned
            User user = userRepository.findByUsername(username)
                    .orElse(null);

            if (user != null && user.isBanned()) {
                log.warn("Banned user {} attempted to access {}", username, request.getRequestURI());

                // Prepare error response
                Map<String, String> error = new HashMap<>();
                error.put("error", "Account Banned");
                error.put("message",
                        user.getBanReason() != null ? user.getBanReason() : "Your account has been banned");

                // Set response
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write(objectMapper.writeValueAsString(error));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}