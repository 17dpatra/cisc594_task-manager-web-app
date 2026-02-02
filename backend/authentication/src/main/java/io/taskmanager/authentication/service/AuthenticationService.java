package io.taskmanager.authentication.service;

import io.taskmanager.authentication.dto.auth.AuthResponse;
import io.taskmanager.authentication.dto.auth.LoginRequest;
import io.taskmanager.authentication.dto.user.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
        String token;
        token = jwtTokenService.createToken(
                (UserPrincipal) authentication.getPrincipal(),
                authentication.getAuthorities()
        );

        return AuthResponse.bearer(token);
    }
}
