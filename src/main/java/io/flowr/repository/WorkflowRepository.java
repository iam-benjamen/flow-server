package io.flowr.repository;

import io.flowr.entity.Organization;
import io.flowr.entity.User;
import io.flowr.entity.Workflow;
import io.flowr.utils.Enums;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, UUID> {
    
    Optional<Workflow> findWorkflowById(UUID id);
    
    List<Workflow> findByTitleContainingIgnoreCase(String title);
    
    List<Workflow> findByStatus(Enums.WorkflowStatus status);
    
    List<Workflow> findByPriority(Enums.Priority priority);
    
    List<Workflow> findByStatusAndPriority(Enums.WorkflowStatus status, Enums.Priority priority);
    
    List<Workflow> findByOrganization(Organization organization);
    
    List<Workflow> findByOrganizationId(UUID organizationId);
    
    Page<Workflow> findByOrganization(Organization organization, Pageable pageable);
    
    List<Workflow> findByInitiatedBy(User user);
    
    List<Workflow> findByInitiatedById(UUID userId);
    
    List<Workflow> findByDueAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<Workflow> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<Workflow> findByDueAtBefore(LocalDateTime date);
    
    @Query("SELECT w FROM Workflow w WHERE w.dueAt < :now AND w.status != :completedStatus")
    List<Workflow> findOverdueWorkflows(@Param("now") LocalDateTime now, 
                                       @Param("completedStatus") Enums.WorkflowStatus completedStatus);
    
    @Query("SELECT w FROM Workflow w WHERE w.organization.id = :orgId AND w.status IN :activeStatuses")
    List<Workflow> findActiveWorkflowsByOrganization(@Param("orgId") UUID organizationId, 
                                                    @Param("activeStatuses") List<Enums.WorkflowStatus> activeStatuses);

    @Query("SELECT DISTINCT w FROM Workflow w WHERE w.initiatedBy.id = :userId")
    List<Workflow> findWorkflowsInitiatedByUser(@Param("userId") UUID userId);

    @Query("""
        SELECT DISTINCT ws.workflow FROM WorkflowStep ws 
        WHERE ws.assignedTo.id = :userId
        """)
    List<Workflow> findWorkflowsAssignedToUser(@Param("userId") UUID userId);

    @Query("""
        SELECT DISTINCT w FROM Workflow w 
        LEFT JOIN FETCH w.initiatedBy 
        WHERE w.initiatedBy.id = :userId 
        OR EXISTS (
            SELECT 1 FROM WorkflowStep ws 
            WHERE ws.workflow.id = w.id 
            AND ws.assignedTo.id = :userId
        )
        ORDER BY w.createdAt DESC
        """)
    List<Workflow> findWorkflowsByUserInvolvement(@Param("userId") UUID userId);

}