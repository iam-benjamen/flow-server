package io.flowr.dto.workflow;

import io.flowr.utils.Enums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

public class WorkflowDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response{
        private UUID id;
        private String title;
        private String description;
        private Enums.WorkflowStatus status;
        private Boolean isActive;
        private Enums.Priority priority;
        private LocalDateTime startedAt;
        private LocalDateTime completedAt;
        private WorkFlowStepSummary currentStep;
        private LocalDateTime dueAt;
        private UserSummary initiatedBy;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserSummary {
        private UUID id;
        private String name;
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WorkFlowStepSummary {
        private UUID id;
        private String name;
        private Integer stepOrder;
        private Enums.StepStatus status;
    }
}
