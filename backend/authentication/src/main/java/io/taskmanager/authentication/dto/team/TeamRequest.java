package io.taskmanager.authentication.dto.team;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record TeamRequest(
        @NotBlank String name,
        Set<TeamMember> addMembers,
        Set<Long> removeUserIds
) {
}
