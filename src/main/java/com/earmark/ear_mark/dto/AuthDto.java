package com.earmark.ear_mark.dto;

import lombok.Data;

public class AuthDto {

    // Request body for POST /api/auth/register
    @Data
    public static class RegisterRequest {
        private String email;
        private String password;
    }

    // Request body for POST /api/auth/login
    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }

    // Response for both register and login — just the JWT
    @Data
    public static class AuthResponse {
        private String token;
        private String email;

        public AuthResponse(String token, String email) {
            this.token = token;
            this.email = email;
        }
    }
}