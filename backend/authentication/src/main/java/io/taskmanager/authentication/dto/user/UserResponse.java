package io.taskmanager.authentication.dto.user;

import java.util.Set;

public record UserResponse(long id,
                           String username,
                           String displayName,
                           Set<UserRole> roles) {
}
