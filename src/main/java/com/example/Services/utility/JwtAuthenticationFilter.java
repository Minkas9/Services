package com.example.Services.utility;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // Get Authorization header
        String header = request.getHeader("Authorization");

        // Check if header contains Bearer token
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7); // Remove "Bearer " prefix

            // Extract username from the token
            String username = jwtUtil.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Validate token
            if (jwtUtil.validateToken(token, userDetails)) {
                // Create authentication object and set it in the SecurityContext
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                // Log invalid token attempts or handle the case where the token is expired or invalid
                logger.warn("Invalid JWT token for user: " + username);
            }
        } else {
            // Log an invalid username or a situation where the username was not found
            logger.warn("JWT token does not contain a valid username.");
            chain.doFilter(request, response);
        }
    }
}