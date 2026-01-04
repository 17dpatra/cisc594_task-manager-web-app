package io.taskmanager.authentication.domain.user;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserTeamId implements Serializable {
    private Long userId;
    private Long teamId;

    protected UserTeamId() {
    }

    public UserTeamId(Long userId, Long teamId) {
        this.userId = userId;
        this.teamId = teamId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getTeamId() {
        return teamId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserTeamId that)) return false;
        return Objects.equals(userId, that.userId) && Objects.equals(teamId, that.teamId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, teamId);
    }
}
