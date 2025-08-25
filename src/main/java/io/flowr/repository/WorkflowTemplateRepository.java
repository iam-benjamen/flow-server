package io.flowr.repository;

import io.flowr.entity.WorkFlowTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WorkflowTemplateRepository extends JpaRepository<WorkFlowTemplate, UUID> {

}
