package com.earmark.ear_mark.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // OncePerRequestFilter guarantees this runs exactly once per request
    // even in edge cases where filters can get called multiple times

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Step 1 — look for the Authorization header
        String authHeader = request.getHeader("Authorization");

        // Missing or no Bearer prefix — no token to check
        // Pass through unauthenticated, not an error at this layer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 2 — strip "Bearer " prefix to get the raw token
        String token = authHeader.substring(7);

        try {
            // Step 3 — extract username; throws if token is tampered or expired
            String username = jwtService.extractUsername(token);

            // Step 4 — only proceed if we got a username AND context is empty
            if (username != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                // Step 5 — validate fully before touching the context
                if (jwtService.isTokenValid(token, username)) {

                    // Step 6 — build the Authentication object
                    // null credentials: token already proved identity
                    // empty list: no roles yet, added in Day 3
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    username, null, List.of()
                            );

                    // Attaches request metadata (IP, session) for audit logs
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    // Step 7 — ONLY NOW does identity go into the context
                    // Everything downstream trusts this user as authenticated
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Invalid token — don't populate context, don't throw
            // Request continues as unauthenticated
            // The authorization layer sends the 401 if endpoint requires auth
        }

        // Always pass to the next filter, authenticated or not
        filterChain.doFilter(request, response);
    }
}
