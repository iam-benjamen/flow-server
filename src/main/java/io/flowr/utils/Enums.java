package io.flowr.utils;

public enum Enums {
    ;

    public enum Role {
        ADMIN("Administrator - Full system access"),
        DESIGNER("Workflow Designer - Can create and manage workflows"),
        STAFF("Staff Member - Can participate in workflows");

        private final String description;

        Role(String description) {
            this.description = description;
        }
    }

    public enum WorkflowStatus {
        DRAFT, ACTIVE, COMPLETED, CANCELLED, PAUSED, FAILED
    }

    public enum StepStatus {
        PENDING, IN_PROGRESS, COMPLETED, SKIPPED, FAILED, CANCELLED
    }

    public enum ActionType {
        FILE_UPLOAD, REVIEW, SIGNATURE
    }

    public enum ActionStatus {
        PENDING, IN_PROGRESS, COMPLETED, SKIPPED
    }

    public enum Priority {
        LOW, MEDIUM, HIGH, URGENT
    }
}
