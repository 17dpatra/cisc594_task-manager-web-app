package io.taskmanager.authentication.service;

import io.taskmanager.authentication.dao.AppUserRepository;
import io.taskmanager.authentication.domain.user.AppUser;
import io.taskmanager.authentication.domain.user.UserRole;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final AppUserRepository users;
    private final PasswordEncoder encoder;

    public RegistrationService(AppUserRepository users,
                               PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @Transactional
    public AppUser register(String username, String rawPassword, String displayName) {
        String u = normalize(username);
        if (users.existsByUsername(u)) {
            throw new IllegalArgumentException("Username already exists");
        }

        AppUser user = new AppUser();
        user.setUsername(u);
        user.setDisplayName(displayName);
        user.getRoles().add(UserRole.USER);
        user.setPasswordHash(encoder.encode(rawPassword));
        user.setEnabled(true);

        return users.save(user);
    }

    private String normalize(String username) {
        return username == null ? null : username.trim().toLowerCase();
    }
}
