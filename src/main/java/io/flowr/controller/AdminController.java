package io.flowr.controller;

import io.flowr.dto.common.ApiResponse;
import io.flowr.dto.user.ProfileDto;
import io.flowr.dto.workflow.WorkflowDto;
import io.flowr.service.UserService;
import io.flowr.service.WorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    private final WorkflowService workflowService;
    private final UserService userService;

    /**
     * GET all workflows in the organization - admin only
     * /api/v1/admin/workflows
     */
    @GetMapping("/workflows")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<WorkflowDto.Response>>> getAllWorkflows() {
        try {
            List<WorkflowDto.Response> workflows = workflowService.getAllWorkflows();
            return ResponseEntity.ok(ApiResponse.success(workflows));
        } catch (RuntimeException e) {
            log.error("Failed to fetch all workflows: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Assign/Change User Role ---> Admin only
     * PUT /api/v1/admin/users/assign-role?id=37458934jdf
     */

    @PutMapping("/users/assign-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> assignRole(@RequestParam UUID id, @Valid @RequestBody ProfileDto.RoleRequest request) {
        try {
            userService.assignRole(id, request);
            return ResponseEntity.ok(ApiResponse.success("User role updated successfully", null));
        } catch (RuntimeException e) {
            log.error("Failed to assign role: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
