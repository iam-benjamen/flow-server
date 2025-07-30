package io.flowr.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;


/**
 * Custom JWT Authentication Token
 * Extends Spring's UsernamePasswordAuthenticationToken to include
 * additional user information from JWT token
 * This makes it easy to access user details anywhere in the application:
 */
public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private final UUID userId;
    private final UUID organizationId;
    private final String role;

    public JwtAuthenticationToken(
            String email,
            Collection<? extends GrantedAuthority> authorities,
            UUID userId,
            UUID organizationId,
            String role
    ) {
        super(email, null, authorities);
        this.userId = userId;
        this.organizationId = organizationId;
        this.role = role;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return (String) getPrincipal();
    }


    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public boolean isDesigner() {
        return "DESIGNER".equals(role);
    }

    public boolean isStaff() {
        return "STAFF".equals(role);
    }

    public boolean canDesignWorkflows() {
        return isAdmin() || isDesigner();
    }

    public boolean canManageUsers() {
        return isAdmin();
    }
}
