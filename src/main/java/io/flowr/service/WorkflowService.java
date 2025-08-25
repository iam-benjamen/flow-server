package io.flowr.service;

import io.flowr.dto.workflow.WorkflowDetailsDto;
import io.flowr.dto.workflow.WorkflowDto;
import io.flowr.entity.Workflow;
import io.flowr.mapper.WorkflowDetailsMapper;
import io.flowr.mapper.WorkflowListMapper;
import io.flowr.repository.WorkflowRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WorkflowService {
    private final WorkflowRepository workflowRepository;
    private final WorkflowDetailsMapper detailsMapper;
    private final WorkflowListMapper listMapper;

    public List<WorkflowDto.Response> getWorkflowsByUserInvolvement(UUID userId) {
        List<Workflow> workflows = workflowRepository.findWorkflowsByUserInvolvement(userId);

        return workflows.stream()
                .map(listMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<WorkflowDto.Response> getAllWorkflows() {
        List<Workflow> workflows = workflowRepository.findAll();

        return workflows.stream()
                .map(listMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    public WorkflowDetailsDto.Response getWorkflowDetails(UUID id) {
        Workflow workflow = workflowRepository.findWorkflowById(id)
                .orElseThrow(() -> new RuntimeException("Workflow not found"));

        return detailsMapper.toResponse(workflow);
    }
}

