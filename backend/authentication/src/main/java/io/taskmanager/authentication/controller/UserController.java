package io.taskmanager.authentication.controller;

import io.taskmanager.authentication.SecurityUtils;
import io.taskmanager.authentication.dto.user.UserRequest;
import io.taskmanager.authentication.dto.user.UserResponse;
import io.taskmanager.authentication.service.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        UserResponse response = userService.createUser(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("id") long id) {
        UserResponse response = userService.getUserById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getLoggedInUserDetails() {
        return getUserById(SecurityUtils.getCurrentUserId());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('GLOBAL_ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        LoggerFactory.getLogger(UserController.class).info("Getting all users, {}", TransactionSynchronizationManager.isActualTransactionActive());
        List<UserResponse> responses = userService.getAllUsers();

        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('GLOBAL_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }
}
