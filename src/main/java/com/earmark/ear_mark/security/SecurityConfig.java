package com.earmark.ear_mark.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF — we're stateless (JWT), not using browser sessions
            // CSRF protection exists for session-cookie-based auth, not JWT
            .csrf(csrf -> csrf.disable())

            // Define which endpoints need auth and which don't
           .authorizeHttpRequests(auth -> auth
            // Public endpoints — no token needed
            .requestMatchers("/api/auth/**", "/api/test/**").permitAll()
            .requestMatchers("/api/auth/**", "/api/test/**", "/api/spotify/callback").permitAll()
            // Everything else requires a valid JWT
            .anyRequest().authenticated()
)

            // Stateless: Spring should never create an HTTP session
            // Each request must carry its own JWT
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // What happens when unauthenticated request hits a protected endpoint
            // — this is what actually sends the 401 we talked about
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                )
            )

            // Slot our JWT filter in BEFORE Spring's built-in filter
            // Order matters — we populate SecurityContextHolder first
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
            

        return http.build();
    }
}