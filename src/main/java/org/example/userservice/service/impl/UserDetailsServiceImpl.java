package org.example.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.userservice.model.User;
import org.example.userservice.model.UserRole;
import org.example.userservice.service.UserService;
import org.example.userservice.utils.Constants;
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
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) {

        final User authenticatedUser = userService.getUserModelByUsername(username);

        if (Objects.isNull(authenticatedUser)) {
            throw new UsernameNotFoundException(Constants.USERNAME_OR_PASSWORD_INVALID_MESSAGE);
        }

        final String authenticatedUsername = authenticatedUser.getUsername();
        final String authenticatedPassword = authenticatedUser.getPassword();
        final UserRole userRole = authenticatedUser.getRole();
        final SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(userRole.name());

        return new org.springframework.security.core.userdetails.User(authenticatedUsername, authenticatedPassword, Collections.singletonList(grantedAuthority));
    }
}
