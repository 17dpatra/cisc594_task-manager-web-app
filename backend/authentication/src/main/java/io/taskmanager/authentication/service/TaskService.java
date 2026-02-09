package io.taskmanager.authentication.service;

import io.taskmanager.authentication.SecurityUtils;
import io.taskmanager.authentication.dao.TaskRepository;
import io.taskmanager.authentication.dao.UserRepository;
import io.taskmanager.authentication.dao.UserTeamMembershipRepository;
import io.taskmanager.authentication.domain.task.Task;
import io.taskmanager.authentication.domain.team.Team;
import io.taskmanager.authentication.domain.user.User;
import io.taskmanager.authentication.domain.user.UserTeamMembership;
import io.taskmanager.authentication.dto.task.TaskRequest;
import io.taskmanager.authentication.dto.task.TaskResponse;
import io.taskmanager.authentication.dto.task.TaskStatus;
import io.taskmanager.authentication.dto.task.TeamTaskResponse;
import io.taskmanager.authentication.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final UserTeamMembershipRepository membershipRepository;

    public TaskService(TaskRepository taskRepository,
                       UserRepository userRepository,
                       UserTeamMembershipRepository membershipRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
    }

    // ---------------------------
    // Create task:
    // creatorUserId in URL, assignedUserId in body
    // ---------------------------
    public TaskResponse createTask(TaskRequest req) {
        User creator = userRepository.getReferenceById(SecurityUtils.getCurrentUserId());

        Task task = new Task();
        task.setTitle(req.name());
        task.setDescription(req.description());
        task.setStatus(req.status() == null ? TaskStatus.CREATED : req.status());
        task.setPriority(req.priority());
        task.setDueDate(req.deadline());
        if (req.assignee() != null) {
            if (req.assignee() != null && req.assignee() > 0L) {
                User assignee = userRepository.findById(req.assignee())
                        .orElseThrow(() -> new NotFoundException("Assigned user not found: " + req.assignee()));
                task.setAssignedTo(assignee);
            } else {
                throw new IllegalArgumentException("assignee must be >= 0");
            }
        }

        task.setCreatedBy(creator);

        Task saved = taskRepository.save(task);
        return toTaskResponse(saved);
    }

    public TaskResponse updateTask(Long taskId, TaskRequest req) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found: " + taskId));

        if (req.name() != null) task.setTitle(req.name());
        if (req.description() != null)  task.setDescription(req.description());
        if (req.status() != null)  task.setStatus(req.status());
        if (req.priority() != null)  task.setPriority(req.priority());
        if (req.deadline() != null)  task.setDueDate(req.deadline());

        if (req.assignee() != null) {
            if (req.assignee() > 0L) {
                User assignee = userRepository.findById(req.assignee())
                        .orElseThrow(() -> new NotFoundException("Assigned user not found: " + req.assignee()));
                task.setAssignedTo(assignee);
            } else {
                throw new IllegalArgumentException("assignee must be >= 0");
            }
        }

        return toTaskResponse(task); // JPA will flush on tx commit
    }

    // ---------------------------
    // Team dashboard (uses user_teams table)
    // userId must be member of teamId
    // then fetch tasks for all members in that team
    // ---------------------------
    @Transactional(readOnly = true)
    public Map<String, List<TeamTaskResponse>> getTeamTasksGroupedByStatus(Long teamId) {
        UserTeamMembership membership = membershipRepository.findByUserIdAndTeamId(SecurityUtils.getCurrentUserId(), teamId)
                .orElseThrow(() -> new NotFoundException("User " + SecurityUtils.getCurrentUserId() + " is not in team " + teamId));

        // get all memberships for team
        List<UserTeamMembership> teamMemberships = membershipRepository.findByTeamId(teamId);
        if (teamMemberships.isEmpty()) {
            return Map.of();
        }

        List<Long> memberIds = teamMemberships.stream()
                .map(m -> m.getUser().getId())
                .distinct()
                .toList();

        List<Task> tasks = taskRepository.findByAssignedToIds(memberIds);

        Map<String, List<TeamTaskResponse>> grouped = new LinkedHashMap<>();
        grouped.put("created", new ArrayList<>());
        grouped.put("in-progress", new ArrayList<>());
        grouped.put("validating", new ArrayList<>());
        grouped.put("completed", new ArrayList<>());

        for (Task task : tasks) {
            String key = toDashboardKey(task.getStatus());
            grouped.get(key).add(toTeamTaskResponse(task, membership.getTeam()));
        }

        return grouped;
    }

    //get tasks grouped by the status for the user
    @Transactional(readOnly = true)
    public Map<String, List<TaskResponse>> getTasksGroupedByStatusForUser(Long userId) {
        //Create userId list with 1 userid
        List<Long> userIdList = new ArrayList<>();
        userIdList.add(userId);
        // Fetch all tasks assigned to this user
        List<Task> tasks = taskRepository.findByAssignedToIds(userIdList);

        // Initialize map with empty lists
        Map<String, List<TaskResponse>> grouped = new LinkedHashMap<>();
        grouped.put("created", new ArrayList<>());
        grouped.put("in-progress", new ArrayList<>());
        grouped.put("validating", new ArrayList<>());
        grouped.put("completed", new ArrayList<>());

        // Populate map
        for (Task task : tasks) {
            String key = toDashboardKey(task.getStatus()); // reuse your existing method
            grouped.get(key).add(toTaskResponse(task));
        }

        return grouped;
    }

    // GET ALL TASKS
    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(this::toTaskResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<String, List<Task>> getTeamTasksByUser(Long userId) {
        List<Long> teamIds = membershipRepository.findTeamIdsByUserId(userId);

        if (teamIds.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, 
                "User is not part of any team"
            );
        }

        // 2. Assuming single team
        Long teamId = teamIds.get(0);

        // 3. Get all user IDs in that team
        List<Long> teamUserIds =
                membershipRepository.findUserIdsByTeamId(teamId);

        // 4. Fetch tasks assigned to team members
        List<Task> tasks =
                taskRepository.findByAssignedToIds(teamUserIds);

        // 5. Group by status (ordered)
        return tasks.stream()
                .sorted(Comparator.comparingInt(this::statusOrder))
                .collect(Collectors.groupingBy(
                        t -> toDashboardKey(t.getStatus()),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    private int statusOrder(Task task) {
        return switch (task.getStatus().name().toLowerCase()) {
            case "created" -> 1;
            case "in-progress" -> 2;
            case "validating" -> 3;
            case "completed" -> 4;
            default -> 5;
        };
    }

    // GET TASK BY ID
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found: " + taskId));

        return toTaskResponse(task);
    }

    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found: " + taskId));

        taskRepository.delete(task);
    }

    private String toDashboardKey(TaskStatus status) {
        if (status == null) return "created";
        return switch (status) {
            case CREATED -> "created";
            case IN_PROGRESS -> "in-progress";
            case VALIDATING -> "validating";
            case COMPLETED -> "completed";
        };
    }

    private TaskResponse toTaskResponse(Task t) {
        return new TaskResponse(
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                t.getStatus(),
                t.getPriority(),
                t.getDueDate(),
                t.getAssignedTo() != null ? t.getAssignedTo().getId() : null
        );
    }

    private TeamTaskResponse toTeamTaskResponse(Task t, Team team) {
        User u = t.getAssignedTo();
        return new TeamTaskResponse(
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                t.getStatus(),
                t.getPriority(),
                t.getDueDate(),

                u != null ? u.getId() : null,
                u != null ? u.getUsername() : null,

                team != null ? team.getId() : null,
                team != null ? team.getName() : null
        );
    }
}