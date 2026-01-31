package io.taskmanager.authentication.dto.user;

import io.taskmanager.authentication.domain.user.UserRole;
import io.taskmanager.authentication.domain.user.UserTeamMembership;

import java.util.Set;

public record UserResponse(long id,
                           String username,
                           String displayName,
                           Set<UserRole> roles) {
}
