package io.flowr.utils;

import io.flowr.config.JwtAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class SecurityUtils {
    public static JwtAuthenticationToken getCurrentJwtAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth;
        }

        throw new IllegalStateException("No authenticated user found");
    }

    public static UUID getCurrentUserId() {
        return getCurrentJwtAuthentication().getUserId();
    }


    public static String getCurrentUserEmail() {
        return getCurrentJwtAuthentication().getEmail();
    }

    public static String getCurrentUserRole() {
        return getCurrentJwtAuthentication().getRole();
    }

    public static UUID getCurrentUserOrganizationId() {
        return getCurrentJwtAuthentication().getOrganizationId();
    }

    public static boolean isCurrentUserAdmin() {
        return getCurrentJwtAuthentication().isAdmin();
    }


    public static boolean canCurrentUserDesignWorkflows() {
        return getCurrentJwtAuthentication().canDesignWorkflows();
    }


    public static boolean canCurrentUserManageUsers() {
        return getCurrentJwtAuthentication().canManageUsers();
    }


    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                authentication.isAuthenticated() &&
                authentication instanceof JwtAuthenticationToken;
    }


    public static boolean isCurrentUserInOrganization(UUID organizationId) {
        if (!isAuthenticated()) {
            return false;
        }
        return getCurrentUserOrganizationId().equals(organizationId);
    }

}
