package io.taskmanager.authentication.dto.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public record UserPrincipal(
        Long id,
        String username,
        boolean enabled,
        Collection<? extends GrantedAuthority> authorities
) implements UserDetails {

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return null; }
    @Override public String getUsername() { return username; }
    @Override public boolean isEnabled() { return enabled; }
}

