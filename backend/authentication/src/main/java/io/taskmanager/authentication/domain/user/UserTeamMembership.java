package io.taskmanager.authentication.domain.user;

import io.swagger.v3.oas.annotations.Hidden;
import io.taskmanager.authentication.domain.team.Team;
import io.taskmanager.authentication.dto.team.TeamRole;
import jakarta.persistence.*;

import java.time.Instant;

@Hidden
@Entity
@Table(name = "user_teams")
public class UserTeamMembership {

    @Column(name = "joined_at", nullable = false, updatable = false)
    private final Instant joinedAt = Instant.now();
    @EmbeddedId
    private UserTeamId id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("teamId")
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TeamRole role = TeamRole.MEMBER;

    protected UserTeamMembership() {
    }

    public UserTeamMembership(User user, Team team, TeamRole role) {
        this.user = user;
        this.team = team;
        this.role = role;
        this.id = new UserTeamId(user.getId(), team.getId());
    }

    @PostPersist
    void ensureId() {
        if (this.id == null && user != null && team != null) {
            this.id = new UserTeamId(user.getId(), team.getId());
        }
    }

    public UserTeamId getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Team getTeam() {
        return team;
    }

    public TeamRole getRole() {
        return role;
    }

    public void setRole(TeamRole role) {
        this.role = role;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }
}
