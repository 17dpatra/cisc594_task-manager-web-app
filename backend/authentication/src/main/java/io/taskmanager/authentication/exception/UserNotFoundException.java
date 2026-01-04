package io.taskmanager.authentication.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) { super("User not found: " + username); }
}
