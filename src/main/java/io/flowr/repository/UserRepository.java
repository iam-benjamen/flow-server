package io.flowr.repository;

import io.flowr.entity.User;
import io.flowr.utils.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    /**
     * Find user by email and active status
     */
    Optional<User> findByEmailAndIsActiveTrue(String email);

    /**
     * Find user by email (regardless of active status)
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if user exists by email
     */
    boolean existsByEmail(String email);

    /**
     * Find user by password hash
     */
    Optional<User> findByPasswordHash(String token);

    /**
     * Find all users in an organization
     */
    List<User> findByOrganizationIdAndIsActiveTrue(UUID organizationId);

    /**
     * Find users by role in an organization
     */
    List<User> findByOrganizationIdAndRoleAndIsActiveTrue(UUID organizationId, Role role);

    /**
     * Count active users in an organization
     */
    long countByOrganizationIdAndIsActiveTrue(UUID organizationId);

    /**
     * Custom query to find users with specific email domain
     * @Query annotation allows custom JPQL queries
     */
    @Query("SELECT u FROM User u WHERE u.email LIKE %:domain% AND u.isActive = true")
    List<User> findByEmailDomain(@Param("domain") String domain);

    /**
     * Find users created in the last N days
     */
    @Query("SELECT u FROM User u WHERE u.createdAt >= CURRENT_TIMESTAMP - :days DAY")
    List<User> findUsersCreatedInLastDays(@Param("days") int days);
}