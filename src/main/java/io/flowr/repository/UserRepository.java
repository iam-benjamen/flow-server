package io.flowr.repository;

import io.flowr.entity.User;
import io.flowr.utils.Enums;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmailAndIsActiveTrue(String email);

    Optional<User> findByEmail(String email);


    boolean existsByEmail(String email);

    Optional<User> findByPasswordHash(String token);

    /**
     * Find all users in an organization
     */
    List<User> findByOrganizationIdAndIsActiveTrue(UUID organizationId);

    /**
     * Find users by role in an organization
     */
    List<User> findByOrganizationIdAndRoleAndIsActiveTrue(UUID organizationId, Enums.Role role);

    /**
     * Count active users in an organization
     */
    long countByOrganizationIdAndIsActiveTrue(UUID organizationId);

    /**
     * Find users created in the last N days
     */
    @Query("SELECT u FROM User u WHERE u.createdAt >= CURRENT_TIMESTAMP - :days DAY")
    List<User> findUsersCreatedInLastDays(@Param("days") int days);
}