package io.taskmanager.authentication.service;

import io.taskmanager.authentication.SecurityUtils;
import io.taskmanager.authentication.dao.UserRepository;
import io.taskmanager.authentication.dao.TeamRepository;
import io.taskmanager.authentication.dao.UserTeamMembershipRepository;
import io.taskmanager.authentication.domain.team.Team;
import io.taskmanager.authentication.dto.team.TeamRole;
import io.taskmanager.authentication.domain.user.User;
import io.taskmanager.authentication.domain.user.UserTeamMembership;
import io.taskmanager.authentication.dto.team.TeamMember;
import io.taskmanager.authentication.dto.team.TeamRequest;
import io.taskmanager.authentication.dto.team.TeamResponse;
import io.taskmanager.authentication.exception.AlreadyExistsException;
import io.taskmanager.authentication.exception.NotAllowedException;
import io.taskmanager.authentication.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamService {

    private final TeamRepository teamRepo;
    private final UserTeamMembershipRepository membershipRepo;
    private final UserRepository userService;

    public TeamService(TeamRepository teamRepo,
                       UserTeamMembershipRepository membershipRepo, UserRepository userService) {
        this.teamRepo = teamRepo;
        this.membershipRepo = membershipRepo;
        this.userService = userService;
    }

    @Transactional
    public List<TeamResponse> getMyTeams() {
        return membershipRepo.findTeamsByUserId(SecurityUtils.getCurrentUserId()).stream()
                .map(TeamService::toResponse)
                .toList();
    }

    @Transactional
    public List<TeamMember> getTeamMembers(Long teamId) {
        teamRepo.findById(teamId).orElseThrow(() -> new NotFoundException(teamId));
        Long requesterId = SecurityUtils.getCurrentUserId();
        membershipRepo.findByUserIdAndTeamId(requesterId, teamId)
                .orElseThrow(() -> new NotAllowedException("You are not a member of this team"));

        return membershipRepo.findByTeamId(teamId).stream()
                .map(m -> new TeamMember(
                        m.getId().getUserId(),
                        m.getRole()
                ))
                .toList();
    }

    @Transactional
    public TeamResponse createTeam(TeamRequest req) {
        try {
            if (!SecurityUtils.isGlobalAdmin()) {
                throw new NotAllowedException("Only admins can create teams");
            }
            Team team = new Team();
            team.setName(req.name().trim());
            team.setCreatedBy(SecurityUtils.getCurrentUserId());

            Team saved = teamRepo.save(team);

            UserTeamMembership membership = new UserTeamMembership(
                    userService.getReferenceById(SecurityUtils.getCurrentUserId()),
                    saved,
                    TeamRole.GLOBAL_ADMIN
            );
            UserTeamMembership savedMembership = membershipRepo.save(membership);
            team.getMemberships().add(savedMembership);
            saved = teamRepo.save(saved);

            return updateTeam(saved.getId(), req);
        }
        catch (DataIntegrityViolationException e) {
            throw new AlreadyExistsException(req.name());
        }
    }

    @Transactional
    public TeamResponse findTeamById(Long teamId) {
        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new NotFoundException("Team not found"));
        Long requesterId = SecurityUtils.getCurrentUserId();
        membershipRepo.findByUserIdAndTeamId(requesterId, teamId)
                .orElseThrow(() -> new NotFoundException(requesterId + " is not a member of this team"));
        return toResponse(team);
    }

    @Transactional
    public TeamResponse updateTeam(Long teamId, TeamRequest req) {
        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new NotFoundException("Team not found"));
        Long requesterId = SecurityUtils.getCurrentUserId();
        UserTeamMembership myMembership = membershipRepo.findByUserIdAndTeamId(requesterId, teamId)
                .orElseThrow(() -> new NotFoundException(requesterId + " is not a member of this team"));

        if (myMembership.getRole() != TeamRole.GLOBAL_ADMIN) {
            throw new NotAllowedException("Team admin privileges required");
        }

        if (req.name() != null && !req.name().isBlank()) {
            team.setName(req.name());
            try {
                teamRepo.save(team);
            }
            catch (DataIntegrityViolationException ex) {
                throw new NotFoundException(req.name());
            }
        }

        if (req.addMembers() != null) {
            for (TeamMember m : req.addMembers()) {
                if (m == null || m.userId() == 0 || m.userId() == SecurityUtils.getCurrentUserId()) continue;


                User user = userService.getReferenceById(m.userId());

                TeamRole desiredRole = (m.role() != null) ? m.role() : TeamRole.USER;

                UserTeamMembership membership = membershipRepo
                        .findByUserIdAndTeamId(m.userId(), teamId)
                        .orElseGet(() -> new UserTeamMembership(user, team, desiredRole));

                membership.setRole(desiredRole);
                UserTeamMembership saved = membershipRepo.save(membership);
                team.getMemberships().add(saved);
            }
        }

        if (req.removeUserIds() != null) {
            for (Long userIdToRemove : req.removeUserIds()) {
                if (userIdToRemove == null) continue;

                if (!SecurityUtils.isGlobalAdmin() && userIdToRemove.equals(requesterId)) {
                    throw new NotFoundException("You cannot remove yourself from the team");
                }

                UserTeamMembership delete = membershipRepo.findByUserIdAndTeamId(userIdToRemove, teamId).orElseThrow(() -> new NotFoundException(userIdToRemove + " is not a member of this team"));
                team.getMemberships().remove(delete);
                membershipRepo.deleteByUserIdAndTeamId(userIdToRemove, teamId);

            }
        }
        return toResponse(teamRepo.save(team));
    }


    @Transactional
    public void deleteTeam(Long teamId) {
        UserTeamMembership myMembership = membershipRepo.findByUserIdAndTeamId(SecurityUtils.getCurrentUserId(), teamId)
                .orElseThrow(() -> new NotFoundException(SecurityUtils.getCurrentUserId() + " is not a member of this team"));

        if (myMembership.getRole() != TeamRole.GLOBAL_ADMIN) {
            throw new NotAllowedException("Team admin privileges required to delete your team");
        }

        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new NotFoundException(teamId));

        teamRepo.delete(team);
    }

    private static TeamResponse toResponse(Team t) {
        return new TeamResponse(
                t.getId(),
                t.getName(),
                t.getCreatedBy(),
                t.getCreatedAt(),
                t.getMemberships().stream().map(it -> new TeamMember(it.getId().getUserId(), it.getRole())).collect(Collectors.toSet())
        );
    }
}
