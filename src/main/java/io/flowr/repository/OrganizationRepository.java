package io.flowr.repository;

import io.flowr.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    Optional<Organization> findByName(String name);

    boolean existsById(UUID id);

    Optional<Organization> findById(UUID id);
}
