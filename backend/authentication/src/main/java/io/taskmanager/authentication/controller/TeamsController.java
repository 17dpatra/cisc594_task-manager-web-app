package io.taskmanager.authentication.controller;


import io.taskmanager.authentication.dto.team.TeamMember;
import io.taskmanager.authentication.dto.team.TeamRequest;
import io.taskmanager.authentication.dto.team.TeamResponse;
import io.taskmanager.authentication.dto.user.UserPrincipal;
import io.taskmanager.authentication.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teams")
public class TeamsController {

    private final TeamService teams;

    public TeamsController(TeamService teams) {
        this.teams = teams;
    }

    @GetMapping
    public List<TeamResponse> getMyTeams() {
        return teams.getMyTeams();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('GLOBAL_ADMIN')")
    public TeamResponse createTeam(
            @AuthenticationPrincipal UserPrincipal me,
            @Valid @RequestBody TeamRequest req) {
        return teams.createTeam(me, req);
    }

    @PutMapping("/{teamId}")
    public TeamResponse updateTeam(
            @PathVariable Long teamId,
            @Valid @RequestBody TeamRequest req) {
        return teams.updateTeam(teamId, req);
    }

    @GetMapping("/{teamId}")
    public TeamResponse findTeamById(@PathVariable Long teamId) {
        return teams.findTeamById(teamId);
    }

    @GetMapping("/{teamId}/users")
    public List<TeamMember> getTeamsUser(@PathVariable Long teamId) {
        return teams.getTeamMembers(teamId);
    }

    @DeleteMapping("/{teamId}")
    public void deleteTeam(@PathVariable Long teamId) {
        teams.deleteTeam(teamId);
    }
}
