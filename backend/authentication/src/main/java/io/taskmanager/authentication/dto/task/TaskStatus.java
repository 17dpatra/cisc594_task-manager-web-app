package io.taskmanager.authentication.dto.task;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TaskStatus {
    CREATED,
    IN_PROGRESS,
    VALIDATING,
    COMPLETED;

    @JsonCreator
    public static TaskStatus from(String value) {
        if (value == null) return null;
        String normalized = value.trim()
                .toUpperCase()
                .replace("-", "_")
                .replace(" ", "_");
        return TaskStatus.valueOf(normalized);
    }
}