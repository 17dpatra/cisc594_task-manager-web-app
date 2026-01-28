package io.taskmanager.authentication.controller;

import java.util.List;
import io.taskmanager.authentication.domain.task.Task;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import io.taskmanager.authentication.service.TaskService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/teams")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/get_tasks")
    public List<Task> getTasks(@RequestParam Long userId) {
        return taskService.getTasksByUser(userId);
    }

    @PutMapping("/update_task")
    public Task updateTask(@RequestBody Task task) {
        return taskService.updateTask(task);
    }
    
    @DeleteMapping("/delete_task/{taskId}")
    public String deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return "Task deleted successfully";
    }
}
