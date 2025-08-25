package io.flowr.controller;


import io.flowr.dto.common.ApiResponse;
import io.flowr.dto.workflow.WorkflowDto;
import io.flowr.service.WorkflowService;
import io.flowr.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workflows")
@RequiredArgsConstructor
@Slf4j
public class WorkflowController {
    private final WorkflowService workflowService;

    /**
     * GET all workflows a user is involved with(My workflow)
     * /api/v1/workflows
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<WorkflowDto.Response>>> getMyWorkflows() {
        UUID currentUserId = SecurityUtils.getCurrentUserId();

        try{
            List<WorkflowDto.Response> workflows = workflowService.getWorkflowsByUserInvolvement(currentUserId);
            return ResponseEntity.ok(ApiResponse.success(workflows));
        } catch (RuntimeException e) {
            log.error("Failed to fetch workflows: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }

    }

}
