package io.taskmanager.authentication.dto.task;


import java.time.LocalDate;

public record TeamTaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        String priority,
        LocalDate dueDate,

        Long userId,
        String username,

        Long teamId,
        String teamName
) {}