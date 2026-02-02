package io.taskmanager.authentication.domain.user;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Hidden
@Embeddable
public class UserTeamId implements Serializable {
    private long userId;
    private long teamId;

    protected UserTeamId() {
    }

    public UserTeamId(long userId, long teamId) {
        this.userId = userId;
        this.teamId = teamId;
    }

    public long getUserId() {
        return userId;
    }

    public long getTeamId() {
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
