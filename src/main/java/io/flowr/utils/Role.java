package io.flowr.utils;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("Administrator - Full system access"),
    DESIGNER("Workflow Designer - Can create and manage workflows"),
    STAFF("Staff Member - Can participate in workflows");

    private final String description;

    Role(String description) {
        this.description = description;
    }
}
