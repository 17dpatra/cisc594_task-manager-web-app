package io.taskmanager.authentication.domain.task;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import io.taskmanager.authentication.domain.user.*;

@Entity
@Table(name = "tasks")
@Hidden
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String status; // TODO, IN_PROGRESS, COMPLETED

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // getters/setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
}
