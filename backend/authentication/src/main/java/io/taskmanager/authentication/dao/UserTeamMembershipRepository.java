package io.taskmanager.authentication.dao;

import io.taskmanager.authentication.domain.team.Team;
import io.taskmanager.authentication.domain.user.UserTeamId;
import io.taskmanager.authentication.domain.user.UserTeamMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserTeamMembershipRepository
        extends JpaRepository<UserTeamMembership, UserTeamId> {

    @Query("""
        SELECT ut.team
        FROM UserTeamMembership ut
        WHERE ut.user.id = :userId
    """)
    List<Team> findTeamsByUserId(Long userId);

    List<UserTeamMembership> findByTeamId(Long teamId);

    Optional<UserTeamMembership> findByUserIdAndTeamId(Long userId, Long teamId);


    void deleteByUserIdAndTeamId(Long userId, Long teamId);

    @Query("SELECT tm.team.id FROM UserTeamMembership tm WHERE tm.user.id = :userId")
    List<Long> findTeamIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT tm.user.id FROM UserTeamMembership tm WHERE tm.team.id = :teamId")
    List<Long> findUserIdsByTeamId(@Param("teamId") Long teamId);
}
