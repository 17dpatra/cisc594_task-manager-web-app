package io.taskmanager.authentication.dto.auth;

import io.taskmanager.authentication.dto.user.UserRole;

import java.util.Set;

public record LoginRequest(String username, String password, Set<UserRole> roles) {
}
