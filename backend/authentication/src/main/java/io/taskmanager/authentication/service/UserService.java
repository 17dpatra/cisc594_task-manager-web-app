package io.taskmanager.authentication.service;

import io.taskmanager.authentication.SecurityUtils;
import io.taskmanager.authentication.controller.UserController;
import io.taskmanager.authentication.dao.AppUserRepository;
import io.taskmanager.authentication.domain.user.User;
import io.taskmanager.authentication.dto.user.UserRole;
import io.taskmanager.authentication.dto.user.UserPrincipal;
import io.taskmanager.authentication.dto.user.UserRequest;
import io.taskmanager.authentication.dto.user.UserResponse;
import io.taskmanager.authentication.exception.NotAllowedException;
import io.taskmanager.authentication.exception.NotFoundException;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.HashSet;
import java.util.List;

@Service
@Transactional
public class UserService implements UserDetailsService {
    private final AppUserRepository userRepository;
    private final PasswordEncoder   passwordEncoder;

    public UserService(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(UserRequest req) {
        String normalizedUsername = normalizeUsername(req.username());

        if (userRepository.existsByUsername(normalizedUsername)) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(normalizedUsername);
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.getRoles().addAll(req.roles());

        User saved = userRepository.save(user);

        return toUserResponse(saved);
    }

    public UserResponse getUserById(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User id not found: " + userId));

        return toUserResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        LoggerFactory.getLogger(UserController.class).info("Getting all users, {}", TransactionSynchronizationManager.isActualTransactionActive());
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(this::toUserResponse)
                .toList();
    }

    public void deleteUser(long userId) {
        if (!SecurityUtils.isGlobalAdmin()) {
            throw new NotFoundException("Only global admins can delete users");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));

        if (user.getRoles().contains(UserRole.GLOBAL_ADMIN)) {
            throw new NotAllowedException("Cannot delete global admin users");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public UserPrincipal loadUserByUsername(String username) throws UsernameNotFoundException {
        String normalizedUsername = normalizeUsername(username);

        User user = userRepository.findByUsername(normalizedUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + normalizedUsername));

        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.isEnabled(),
                user.getRoles().stream()
                        .map(Enum::name)
                        .map(SimpleGrantedAuthority::new)
                        .toList()
        );
    }

    private String normalizeUsername(String username) {
        String value = username;

        if (value == null) {
            value = "";
        }

        value = value.trim().toLowerCase();

        if (value.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        return value;
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                new HashSet<>(user.getRoles())
        );
    }
}
