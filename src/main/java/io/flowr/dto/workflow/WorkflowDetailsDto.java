package io.flowr.dto.workflow;

import io.flowr.utils.Enums;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class WorkflowDetailsDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private UUID id;
        private String title;
        private String description;
        private Enums.WorkflowStatus status;
        private Boolean isActive;
        private Enums.Priority priority;
        private LocalDateTime startedAt;
        private LocalDateTime completedAt;
        private LocalDateTime dueAt;
        private LocalDateTime createdAt;
        private UserSummary initiatedBy;
        private List<StepDetails> steps;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StepDetails {
        private UUID id;
        private String name;
        private String description;
        private Integer stepOrder;
        private Enums.StepStatus status;
        private String comment;
        private LocalDateTime assignedAt;
        private LocalDateTime createdAt;
        private LocalDateTime dueAt;
        private LocalDateTime completedAt;
        private UserSummary assignedTo;
        private List<ActionDetails> actions;
        private Boolean isCurrentStep;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ActionDetails {
        private UUID id;
        private String name;
        private String description;
        private Enums.ActionType actionType;
        private Enums.ActionStatus status;
        private Integer actionOrder;
        private Boolean isOptional;
        private LocalDateTime completedAt;
        private String actionData;
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
}