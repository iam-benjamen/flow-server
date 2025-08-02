package io.flowr.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;


@Service
@Slf4j
public class JwtService {

    private final SecretKey secretKey;
    private final long jwtExpirationHours;
    private final long emailVerificationExpirationHours;
    private final long passwordResetExpirationHours;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration.hours}") long jwtExpirationHours,
            @Value("${jwt.email-verification.expiration.hours}") long emailVerificationExpirationHours,
            @Value("${jwt.password-reset.expiration.hours}") long passwordResetExpirationHours
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpirationHours = jwtExpirationHours;
        this.emailVerificationExpirationHours = emailVerificationExpirationHours;
        this.passwordResetExpirationHours = passwordResetExpirationHours;
    }


    public String generateToken(String email, String userId, String role, String organizationId) {
        Instant now = Instant.now();
        Instant expiration = now.plus(jwtExpirationHours, ChronoUnit.HOURS);

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("role", role)
                .claim("organizationId", organizationId)
                .claim("type", "auth")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    public String generateEmailVerificationToken(String email, String userId) {
        Instant now = Instant.now();
        Instant expiration = now.plus(emailVerificationExpirationHours, ChronoUnit.HOURS);

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("email", email)
                .claim("type", "email_verification")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }


    public String generatePasswordResetToken(String email, String userId) {
        Instant now = Instant.now();
        Instant expiration = now.plus(passwordResetExpirationHours, ChronoUnit.HOURS);

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("email", email)
                .claim("type", "password_reset")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }


    public Claims validateAuthToken(String token) {
        Claims claims = validateTokenAndGetClaims(token);

        String tokenType = claims.get("type", String.class);
        if (!"auth".equals(tokenType)) {
            throw new JwtException("Invalid token type for authentication");
        }

        return claims;
    }


    public Claims validateEmailVerificationToken(String token) {
        Claims claims = validateTokenAndGetClaims(token);

        String tokenType = claims.get("type", String.class);
        if (!"email_verification".equals(tokenType)) {
            throw new JwtException("Invalid token type for email verification");
        }

        return claims;
    }

    public Claims validatePasswordResetToken(String token) {
        Claims claims = validateTokenAndGetClaims(token);

        String tokenType = claims.get("type", String.class);
        if (!"password_reset".equals(tokenType)) {
            throw new JwtException("Invalid token type for password reset");
        }

        return claims;
    }

    private Claims validateTokenAndGetClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
            throw new JwtException("Token has expired");
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
            throw new JwtException("Unsupported token");
        } catch (MalformedJwtException e) {
            log.warn("JWT token is invalid: {}", e.getMessage());
            throw new JwtException("Invalid token format");
        } catch (SecurityException e) {
            log.warn("JWT signature validation failed: {}", e.getMessage());
            throw new JwtException("Invalid token signature");
        } catch (IllegalArgumentException e) {
            log.warn("JWT token compact of handler are invalid: {}", e.getMessage());
            throw new JwtException("Invalid token");
        }
    }


    public String extractEmail(String token) {
        Claims claims = validateTokenAndGetClaims(token);
        return claims.getSubject();
    }


    public String extractUserId(String token) {
        Claims claims = validateTokenAndGetClaims(token);
        return claims.get("userId", String.class);
    }


    public String extractRole(String token) {
        Claims claims = validateAuthToken(token);
        return claims.get("role", String.class);
    }


    public String extractOrganizationId(String token) {
        Claims claims = validateAuthToken(token);
        return claims.get("organizationId", String.class);
    }

    public boolean isTokenValid(String token, String email) {
        Claims claims = validateTokenAndGetClaims(token);
        String tokenEmail = claims.getSubject();
        return tokenEmail.equals(email) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = validateTokenAndGetClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    public Date getExpirationDate(String token) {
        Claims claims = validateTokenAndGetClaims(token);
        return claims.getExpiration();
    }
}