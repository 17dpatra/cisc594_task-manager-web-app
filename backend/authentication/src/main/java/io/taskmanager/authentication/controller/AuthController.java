package io.taskmanager.authentication.controller;

import io.taskmanager.authentication.domain.user.AppUser;
import io.taskmanager.authentication.domain.user.UserResponse;
import io.taskmanager.authentication.service.RegistrationService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegistrationService registrationService;

    public AuthController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    public record RegisterRequest(
            @NotBlank @Size(min = 3, max = 50) String username,
            @NotBlank @Size(min = 8, max = 100) String password,
            @Size(max = 100) String displayName
    ) {
    }


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@RequestBody RegisterRequest req) {
        AppUser u = registrationService.register(req.username(), req.password(), req.displayName());
        return new UserResponse(u.getId(), u.getUsername(), u.getDisplayName(), u.getRoles());
    }
}

