package com.earmark.ear_mark.service;

import com.earmark.ear_mark.dto.AuthDto;
import com.earmark.ear_mark.model.User;
import com.earmark.ear_mark.repository.UserRepository;
import com.earmark.ear_mark.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    // BCrypt is the industry standard for password hashing
    // strength 12 means 2^12 rounds of hashing — slow enough to
    // resist brute force, fast enough for normal login use
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    public AuthDto.AuthResponse register(AuthDto.RegisterRequest request) {

        // Check if email is already registered
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Never store plain text passwords — hash before saving
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        // Generate a JWT for the new user so they're logged in immediately
        String token = jwtService.generateToken(user.getEmail());
        return new AuthDto.AuthResponse(token, user.getEmail());
    }

    public AuthDto.AuthResponse login(AuthDto.LoginRequest request) {

        // Find the user — throw if not found
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Compare the submitted password against the stored hash
        // BCrypt handles the comparison safely — never compare plain text
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail());
        return new AuthDto.AuthResponse(token, user.getEmail());
    }
}