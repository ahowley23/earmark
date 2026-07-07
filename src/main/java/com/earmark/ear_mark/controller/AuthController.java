package com.earmark.ear_mark.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.earmark.ear_mark.dto.AuthDto;
import com.earmark.ear_mark.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/register
    // Body: { "email": "...", "password": "..." }
    // Returns: { "token": "...", "email": "..." }
    @PostMapping("/register")
    public ResponseEntity<AuthDto.AuthResponse> register(
            @RequestBody AuthDto.RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // POST /api/auth/login
    // Body: { "email": "...", "password": "..." }
    // Returns: { "token": "...", "email": "..." }
    @PostMapping("/login")
    public ResponseEntity<AuthDto.AuthResponse> login(
            @RequestBody AuthDto.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
