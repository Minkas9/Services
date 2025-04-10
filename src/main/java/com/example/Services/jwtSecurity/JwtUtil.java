package com.example.Services.jwtSecurity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for handling JWT (JSON Web Token) operations.
 * This class provides functionality for:
 * - Generating JWT tokens
 * - Validating tokens
 * - Extracting information from tokens
 * - Managing token expiration
 */
@Slf4j
@Component
public class JwtUtil {

    // Secret key for signing JWT tokens
    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    // Token validity duration (5 hours in milliseconds)
    private static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60 * 1000;

    /**
     * Generates a JWT token for a given user.
     * The token includes the username and is signed with the secret key.
     *
     * @param userDetails The user details to generate the token for
     * @return The generated JWT token as a string
     */
    public String generateToken(UserDetails userDetails) {
        log.info("Generating JWT token for user: {}", userDetails.getUsername());
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .toList());
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Creates a JWT token with the given claims and subject.
     * Sets the token's issue date and expiration date.
     *
     * @param claims  Additional claims to include in the token
     * @param subject The subject of the token (usually username)
     * @return The created JWT token as a string
     */
    private String createToken(Map<String, Object> claims, String subject) {
        log.debug("Creating JWT token for subject: {} with claims: {}", subject, claims);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(key)
                .compact();
    }

    /**
     * Validates a JWT token by checking its signature and expiration.
     *
     * @param token The JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        log.debug("Validating JWT token");
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            boolean isValid = !isTokenExpired(token);
            if (isValid) {
                log.debug("Token is valid");
            } else {
                log.warn("Token has expired");
            }
            return isValid;
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extracts the username from a JWT token.
     * Returns null if the token is invalid or cannot be parsed.
     *
     * @param token The JWT token
     * @return The username stored in the token, or null if the token is invalid
     */
    public String extractUsername(String token) {
        try {
            String username = extractClaim(token, Claims::getSubject);
            log.debug("Extracted username from token: {}", username);
            return username;
        } catch (Exception e) {
            log.warn("Failed to extract username from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extracts the expiration date from a JWT token.
     *
     * @param token The JWT token
     * @return The expiration date of the token
     */
    public Date extractExpiration(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        log.debug("Token expiration date: {}", expiration);
        return expiration;
    }

    /**
     * Generic method to extract a claim from a JWT token.
     *
     * @param token          The JWT token
     * @param claimsResolver Function to extract the specific claim
     * @return The extracted claim
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from a JWT token.
     *
     * @param token The JWT token
     * @return All claims from the token
     * @throws Exception if token parsing fails
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("Error extracting claims from token: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Checks if a JWT token has expired.
     *
     * @param token The JWT token to check
     * @return true if the token has expired, false otherwise
     */
    private Boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        boolean expired = expiration.before(new Date());
        if (expired) {
            log.warn("Token expired at: {}", expiration);
        }
        return expired;
    }
}