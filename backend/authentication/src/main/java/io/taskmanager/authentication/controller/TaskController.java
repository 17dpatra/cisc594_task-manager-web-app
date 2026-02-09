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
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    // GET ALL
    // GET /api/v1/tasks/get_tasks/team/{teamId}
    @GetMapping("/get_tasks/team/{teamId}")
    public ResponseEntity<Map<String, List<TeamTaskResponse>>> getAllTasks(@PathVariable Long teamId) {
        return ResponseEntity.ok(taskService.getTeamTasksGroupedByStatus(teamId));
    }

    @GetMapping("/user/team/{userId}")
    public ResponseEntity<Map<String, List<Task>>> getTeamTasks(
        @PathVariable Long userId) {

    return ResponseEntity.ok(
            taskService.getTeamTasksByUser(userId)
    );
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