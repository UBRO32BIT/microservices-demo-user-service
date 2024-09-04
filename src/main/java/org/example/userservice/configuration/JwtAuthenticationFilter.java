package org.example.userservice.configuration;

import lombok.RequiredArgsConstructor;
import org.example.userservice.utils.JwtUtils;
import org.example.userservice.utils.Constants;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtTokenManager;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest req,
            @NonNull HttpServletResponse res,
            @NonNull FilterChain chain) throws IOException, ServletException {

        final String header = req.getHeader(Constants.HEADER_STRING);
        String username = null;
        String authToken = null;

        if (header != null && header.startsWith(Constants.TOKEN_PREFIX)) {
            authToken = header.substring(Constants.TOKEN_PREFIX.length());
            try {
                username = jwtTokenManager.extractUsername(authToken);
            } catch (Exception e) {
                authenticationEntryPoint.commence(req, res, new BadCredentialsException("Invalid access token"));
                return;
            }
        }

        final SecurityContext securityContext = SecurityContextHolder.getContext();

        if (username != null && securityContext.getAuthentication() == null) {
            final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtTokenManager.validateToken(authToken, userDetails.getUsername())) {
                final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                System.out.println("Authentication successful. Logged in username: " + username);
                securityContext.setAuthentication(authentication);
            }
        }

        chain.doFilter(req, res);
    }
}