package io.taskmanager.authentication.dto.team;

import java.time.Instant;
import java.util.Set;

public record TeamResponse(
        Long id,
        String name,
        Long createdBy,
        Instant createdAt,
        Set<TeamMember> members
) {}
