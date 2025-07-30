package io.flowr.config;

import io.flowr.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * This filter runs on EVERY request to check for JWT tokens
 * Flow:
 * 1. Extract JWT from Authorization header
 * 2. Validate the token
 * 3. Extract user info from token
 * 4. Set Spring Security context
 * 5. Continue to next filter
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
            ) throws ServletException, IOException {
        if(isPublicEndpoint(request.getServletPath())) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if(authHeader == null ||!authHeader.startsWith("Bearer ")) {
            log.debug("Authorization header not present:{}", request.getServletPath());
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try{
            userEmail = jwtService.extractUsername(jwt);

            if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if(jwtService.isTokenValid(jwt, userEmail)){
                    String userId = jwtService.extractUserId(jwt);
                    String role = jwtService.extractRole(jwt);
                    String organizationId = jwtService.extractOrganizationId(jwt);

                    List<SimpleGrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_" + role)
                    );

                    JwtAuthenticationToken authToken = new JwtAuthenticationToken(
                            userEmail,
                            authorities,
                            UUID.fromString(userId),
                            UUID.fromString(organizationId),
                            role
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.debug("Successfully authenticated user: {} with role: {}", userEmail, role);
                }
            }
        } catch (Exception e){
            log.error("Error Parsing JWT Token: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Check if the endpoint is public (doesn't require authentication)
     */
    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/api/v1/auth/") ||
                path.equals("/api/v1/health") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.equals("/favicon.ico");
    }
 }
