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
        log.debug("Processing banned user check for request: {}", request.getRequestURI());

        // Skip banned user check for authentication endpoints and Swagger UI
        if (request.getRequestURI().startsWith("/api/auth/") ||
                request.getRequestURI().startsWith("/swagger-ui/") ||
                request.getRequestURI().startsWith("/v3/api-docs/") ||
                request.getRequestURI().equals("/swagger-ui.html")) {
            log.debug("Skipping banned user check for public endpoint");
            filterChain.doFilter(request, response);
            return;
        }

        // Gaunama autentifikacijos informacija apie dabartinį vartotoją
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            log.debug("Checking ban status for user: {}", username);

            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null && user.isBanned()) {
                log.warn("Banned user {} attempted to access {}", username, request.getRequestURI());

                Map<String, String> error = new HashMap<>();
                error.put("error", "Account Banned");
                error.put("message",
                        user.getBanReason() != null ? user.getBanReason() : "Your account has been banned");

                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write(objectMapper.writeValueAsString(error));
                return;
            }
            log.debug("User {} is not banned, proceeding with request", username);
        } else {
            log.debug("No authenticated user found, skipping ban check");
        }

        // Jei viskas tvarkoje – perduodam kontrolę tolesniems filtrams / endpoint'ui
        filterChain.doFilter(request, response);
    }
}