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

/**
 * Filter that checks if a user is banned before allowing access to protected
 * resources.
 * This filter:
 * - Skips checks for authentication endpoints
 * - Retrieves the authenticated user from the security context
 * - Checks if the user is banned in the database
 * - Returns a 403 Forbidden response with ban reason if the user is banned
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BannedUserFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    /**
     * Processes each request to check if the authenticated user is banned.
     * The filter:
     * 1. Skips check for authentication endpoints
     * 2. Gets the authenticated user from security context
     * 3. Checks if the user is banned in the database
     * 4. Returns 403 Forbidden if the user is banned
     *
     * @param request     HTTP request
     * @param response    HTTP response
     * @param filterChain Filter chain to continue processing
     * @throws ServletException if servlet error occurs
     * @throws IOException      if I/O error occurs
     */
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

        filterChain.doFilter(request, response);
    }
}