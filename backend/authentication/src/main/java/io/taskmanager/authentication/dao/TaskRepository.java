package io.taskmanager.authentication.dao;

import io.taskmanager.authentication.domain.task.Task;
import io.taskmanager.authentication.dto.task.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("""
        SELECT t
        FROM Task t
        WHERE t.assignedTo.id = :userId
    """)
    List<Task> findByAssigneeId(Long userId);

    @Query("""
        SELECT COUNT(t) > 0
        FROM Task t
        WHERE t.id = :taskId
          AND t.assignedTo.id = :userId
    """)
    boolean existsByAssigneeAndTaskId(Long userId, Long taskId);

    @Modifying
    @Query("""
        DELETE FROM Task t
        WHERE t.id = :taskId
          AND t.assignedTo.id = :userId
    """)
    void deleteByAssigneeAndTaskId(Long userId, Long taskId);

    @Query("""
        SELECT t
        FROM Task t
        WHERE t.status = :status
          AND t.assignedTo.id = :userId
    """)
    List<Task> filterTasks(TaskStatus status, Long userId);

    // ✅ Team dashboard: tasks assigned to any member of the team
    @Query("""
        SELECT t
        FROM Task t
        WHERE t.assignedTo.id IN :userIds
    """)
    List<Task> findByAssignedToIds(Collection<Long> userIds);
}