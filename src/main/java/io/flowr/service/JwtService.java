package io.flowr.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


/**
 * JWT Service handles JWT token creation, validation, and extraction
 */
@Service
@Slf4j
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Generate JWT token for a user
     * @param email User's email (becomes the 'subject')
     * @param userId User's UUID
     * @param role User's role
     * @param organizationId User's organization UUID
     * @return JWT token string
     */
    public String generateToken(String email, String userId, String role, String organizationId) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", userId);
        extraClaims.put("role", role);
        extraClaims.put("organizationId", organizationId);

        return generateToken(extraClaims, email);
    }

    /**
     * Generate token with custom claims
     */
    public String generateToken(Map<String, Object> extraClaims, String subject) {
        return buildToken(extraClaims, subject, jwtExpiration);
    }


    /**
     * Build the actual JWT token
     */
    private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    /**
    * Generic method to extract any claim from token
    */
    public <T> T extractClaim(String token,
                              Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

    /**
     * Extract username (email) from token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract user ID from token
     */
    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", String.class));
    }

    /**
     * Extract a user role from token
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    /**
     * Extract organization ID from token
     */
    public String extractOrganizationId(String token) {
        return extractClaim(token, claims -> claims.get("organizationId", String.class));
    }

    /**
     * Extract expiration date from token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Check if the token is expired
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validate token against user details
     */
    public boolean isTokenValid(String token, String username) {
        final String tokenUsername = extractUsername(token);
        return (tokenUsername.equals(username)) && !isTokenExpired(token);
    }

    /**
     * Get the signing key from secret
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
