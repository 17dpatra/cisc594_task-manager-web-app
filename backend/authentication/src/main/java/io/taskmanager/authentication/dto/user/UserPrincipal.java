package io.taskmanager.authentication.dto.user;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Hidden
public record UserPrincipal(
        Long id,
        String username,
        String hashedPassword,
        boolean enabled,
        Collection<? extends GrantedAuthority> authorities
) implements UserDetails {

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return hashedPassword; }
    @Override public String getUsername() { return username; }
    @Override public boolean isEnabled() { return enabled; }
}

