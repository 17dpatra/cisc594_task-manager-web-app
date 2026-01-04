package io.taskmanager.authentication.domain.team;

import io.taskmanager.authentication.domain.user.AppUser;
import io.taskmanager.authentication.domain.user.UserTeamMembership;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private AppUser createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserTeamMembership> memberships = new HashSet<>();

    // getters/setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public AppUser getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Set<UserTeamMembership> getMemberships() {
        return memberships;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreatedBy(AppUser createdBy) {
        this.createdBy = createdBy;
    }
}
