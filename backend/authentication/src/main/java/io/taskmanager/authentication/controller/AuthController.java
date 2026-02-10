package io.taskmanager.authentication.controller;

import io.taskmanager.authentication.dto.auth.AuthResponse;
import io.taskmanager.authentication.dto.auth.LoginRequest;
import io.taskmanager.authentication.dto.user.UserRequest;
import io.taskmanager.authentication.dto.user.UserResponse;
import io.taskmanager.authentication.service.AuthenticationService;
import io.taskmanager.authentication.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    public AuthController(AuthenticationService authenticationService, UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> createUser(@RequestBody UserRequest request) {
        UserResponse response = userService.createUser(request);
        return login(new LoginRequest(response.username(), request.password(), request.roles()));
    }
}

