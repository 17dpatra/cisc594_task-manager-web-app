package io.taskmanager.authentication.service;

import io.taskmanager.authentication.dto.auth.AuthResponse;
import io.taskmanager.authentication.dto.auth.LoginRequest;
import io.taskmanager.authentication.dto.team.TeamRole;
import io.taskmanager.authentication.dto.user.UserPrincipal;
import io.taskmanager.authentication.dto.user.UserRole;
import io.taskmanager.authentication.exception.NotAllowedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    public AuthenticationService(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication;
        authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        for (UserRole role : request.roles()) {
            boolean hasRole = userPrincipal.authorities().contains(new SimpleGrantedAuthority(role.name()));
            if (role == UserRole.GLOBAL_ADMIN && !hasRole) {
                throw new NotAllowedException("Admin privileges required to login as administrator");
            }
        }
        String token;
        token = jwtTokenService.createToken(
                (UserPrincipal) authentication.getPrincipal(),
                authentication.getAuthorities()
        );

        return AuthResponse.bearer(token);
    }
}
