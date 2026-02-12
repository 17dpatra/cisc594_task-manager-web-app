package io.taskmanager.authentication.controller;

import io.taskmanager.authentication.domain.task.Task;
import io.taskmanager.authentication.dto.task.TaskRequest;
import io.taskmanager.authentication.dto.task.TaskResponse;
import io.taskmanager.authentication.dto.task.TeamTaskResponse;
import io.taskmanager.authentication.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api/v2/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // CREATE
    @PostMapping("/create_task")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest taskRequest) {
        return ResponseEntity.ok(taskService.createTask(taskRequest));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskRequest taskRequest
    ) {
        return ResponseEntity.ok(taskService.updateTask(taskId, taskRequest));
    }

    // GET ALL
    // GET /api/v1/tasks/get_tasks
    @GetMapping("/get_tasks")
    public ResponseEntity<List<TaskResponse>> getTasksForUser(
        @RequestParam Long userId
    ) {
        return ResponseEntity.ok(taskService.getTasksForUser(userId));
    }

    @GetMapping("/get_tasks_grouped_for_user")
    public ResponseEntity<Map<String, List<TaskResponse>>> getTasksGroupedForUser(
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(taskService.getTasksGroupedByStatusForUser(userId));
    }

    // GET ALL
    // GET /api/v1/tasks/get_tasks/team/{teamId}
    @GetMapping("/get_tasks/team/{teamId}")
    public ResponseEntity<Map<String, List<TeamTaskResponse>>> getAllTasks(@PathVariable Long teamId) {
        return ResponseEntity.ok(taskService.getTeamTasksGroupedByStatus(teamId));
    }

    @GetMapping("/user/team/{userId}")
    public ResponseEntity<Map<String, Object>> getTeamTasks(@PathVariable Long userId) {
        Map<String, Object> body = new LinkedHashMap<>();
        try {
            Map<String, List<Task>> tasks = taskService.getTeamTasksByUser(userId);
            body.put("data", tasks);
            body.put("status", 200);
            body.put("timestamp", Instant.now());
            return ResponseEntity.ok(body);
        } catch (ResponseStatusException ex) {
            body.put("error", ex.getReason());
            body.put("status", ex.getStatusCode().value());
            body.put("timestamp", Instant.now());
            return ResponseEntity.status(ex.getStatusCode()).body(body);
        } catch (Exception ex) {
            body.put("error", "Internal Server Error");
            body.put("message", ex.getMessage());
            body.put("status", 500);
            body.put("timestamp", Instant.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }


    // GET BY ID
    // GET /api/v1/tasks/{taskId}
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable("taskId") Long taskId) {
        return ResponseEntity.ok(taskService.getTaskById(taskId));
    }

    // DELETE
    // DELETE /api/v1/tasks/{taskId}
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable("taskId") Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}