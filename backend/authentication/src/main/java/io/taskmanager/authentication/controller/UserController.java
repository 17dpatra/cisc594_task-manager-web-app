package io.taskmanager.authentication.controller;

import io.taskmanager.authentication.SecurityUtils;
import io.taskmanager.authentication.dto.task.TeamTaskResponse;
import io.taskmanager.authentication.dto.user.UserRequest;
import io.taskmanager.authentication.dto.user.UserResponse;
import io.taskmanager.authentication.service.TaskService;
import io.taskmanager.authentication.service.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;
    private final TaskService taskService;

    public UserController(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
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
        List<UserResponse> responses = userService.getAllUsers();
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tasks/teams/{teamId}")
    public Map<String, List<TeamTaskResponse>> getTeamTasksGrouped(@PathVariable Long teamId) {
        return taskService.getTeamTasksGroupedByStatus(teamId);
    }
}
