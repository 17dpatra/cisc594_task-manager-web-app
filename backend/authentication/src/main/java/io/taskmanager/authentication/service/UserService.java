package io.taskmanager.authentication.service;

import io.taskmanager.authentication.dao.AppUserRepository;
import io.taskmanager.authentication.domain.user.AppUser;
import io.taskmanager.authentication.domain.user.CreateUserRequest;
import io.taskmanager.authentication.domain.user.UserResponse;
import jakarta.transaction.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserService implements UserDetailsService {
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(CreateUserRequest req) {
        if (userRepository.existsByUsername(req.username())) {
            throw new IllegalArgumentException("Username already exists");
        }

        AppUser user = new AppUser();
        user.setUsername(req.username());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.getRoles().addAll(req.roles());

        AppUser saved = userRepository.save(user);

        return new UserResponse(saved.getId(), saved.getUsername(), saved.getDisplayName(), saved.getRoles());
    }

    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username.trim().toLowerCase())
                .map(u -> User.withUsername(u.getUsername())
                        .password(u.getPasswordHash())
                        .disabled(!u.isEnabled())
                        .authorities(
                                u.getRoles().stream()
                                        .map(Enum::name)
                                        .map(SimpleGrantedAuthority::new)
                                        .toList()
                        )
                        .build()
                )
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));
    }
}
