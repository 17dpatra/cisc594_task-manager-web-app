package io.taskmanager.authentication.domain.user;

import java.util.Set;

public record UserResponse(Long id, String username, String displayName, Set<UserRole> roles) {
}
