package com.example.Services.jwtSecurity;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * Main security configuration class that sets up Spring Security for the
 * application.
 * This class configures:
 * - Which endpoints are public/private
 * - How authentication is handled
 * - Custom security filters
 * - Error handling for unauthorized access
 */
@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final BannedUserFilter bannedUserFilter;
        private final ObjectMapper objectMapper;

        /**
         * Configures the security filter chain with all necessary security settings.
         * This includes:
         * - Disabling CSRF (as we're using JWT)
         * - Configuring public and protected endpoints
         * - Setting up JWT and banned user filters
         * - Configuring custom error handling
         *
         * @param http HttpSecurity object to configure
         * @return Configured SecurityFilterChain
         * @throws Exception if security configuration fails
         */
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                // Disable CSRF as we're using JWT tokens
                                .csrf(csrf -> csrf.disable())
                                // Disable default security headers
                                .headers(headers -> headers.disable())
                                // Configure endpoint security
                                .authorizeHttpRequests(auth -> auth
                                                // Public endpoints that don't require authentication
                                                .requestMatchers("/api/auth/**").permitAll()
                                                .requestMatchers("/h2-console/**").permitAll()
                                                .requestMatchers("/v3/api-docs/**").permitAll()
                                                .requestMatchers("/swagger-ui/**").permitAll()
                                                .requestMatchers("/swagger-ui.html").permitAll()
                                                // Admin-only endpoints
                                                .requestMatchers("/api/service/add").hasRole("ADMIN")
                                                .requestMatchers("/api/service/update/**").hasRole("ADMIN")
                                                .requestMatchers("/api/service/delete/**").hasRole("ADMIN")
                                                // All other endpoints require authentication
                                                .anyRequest().authenticated())
                                // Configure session management (stateless)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                // Configure custom error handling
                                .exceptionHandling(exception -> exception
                                                // Handle unauthorized access (no token or invalid token)
                                                .authenticationEntryPoint((request, response, authException) -> {
                                                        log.warn("Unauthorized access attempt to {}: {}",
                                                                        request.getRequestURI(),
                                                                        authException.getMessage());

                                                        Map<String, String> error = new HashMap<>();
                                                        error.put("error", "Unauthorized");
                                                        error.put("message",
                                                                        "You are not authorized to access this resource");

                                                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                                                        response.getWriter()
                                                                        .write(objectMapper.writeValueAsString(error));
                                                })
                                                // Handle access denied (valid token but insufficient permissions)
                                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                                        log.warn("Access denied for {}: {}",
                                                                        request.getRequestURI(),
                                                                        accessDeniedException.getMessage());

                                                        Map<String, String> error = new HashMap<>();
                                                        error.put("error", "Access Denied");
                                                        error.put("message",
                                                                        "You don't have permission to access this resource");

                                                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                                                        response.getWriter()
                                                                        .write(objectMapper.writeValueAsString(error));
                                                }))
                                // Add JWT filter before the default authentication filter
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                                // Add banned user filter after JWT filter
                                .addFilterAfter(bannedUserFilter, JwtAuthenticationFilter.class);

                return http.build();
        }
}