package io.flowr.repository;

import io.flowr.entity.WorkflowStepAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WorkflowStepActionRepository extends JpaRepository<WorkflowStepAction, UUID> {
}
