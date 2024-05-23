package org.example.userservice.service;

import lombok.RequiredArgsConstructor;
import org.example.userservice.dto.response.UserResponse;
import org.example.userservice.model.User;
import org.example.userservice.model.UserRole;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final String USERNAME_OR_PASSWORD_INVALID = "Invalid username or password.";

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) {

        final User authenticatedUser = userService.getUserModelByUsername(username);

        if (Objects.isNull(authenticatedUser)) {
            throw new UsernameNotFoundException(USERNAME_OR_PASSWORD_INVALID);
        }

        final String authenticatedUsername = authenticatedUser.getUsername();
        final String authenticatedPassword = authenticatedUser.getPassword();
        final UserRole userRole = authenticatedUser.getRole();
        final SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(userRole.name());

        return new org.springframework.security.core.userdetails.User(authenticatedUsername, authenticatedPassword, Collections.singletonList(grantedAuthority));
    }
}
