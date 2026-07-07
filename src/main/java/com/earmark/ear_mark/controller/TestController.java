package com.earmark.ear_mark.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.earmark.ear_mark.security.JwtService;
import com.earmark.ear_mark.service.ClaudeService;
import com.earmark.ear_mark.service.SpotifyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestController {

    private final JwtService jwtService;
    private final SpotifyService spotifyService;
    private final ClaudeService claudeService;

    @GetMapping("/auth/ping")
    public String publicPing() {
        return "public endpoint — no token needed";
    }

    @GetMapping("/auth/token")
    public String getTestToken(@RequestParam String username) {
        return jwtService.generateToken(username);
    }

    @GetMapping("/private/ping")
    public String privatePing() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return "protected endpoint — authenticated as: " + username;
    }

    @GetMapping("/test/spotify")
    public Object testSpotify(@RequestParam String token) {
        return spotifyService.getTopArtists(token, "medium_term");
    }

    @GetMapping("/test/recommend")
    public Object testRecommend(@RequestParam String token) {
        var artists = spotifyService.getTopArtists(token, "medium_term");
        return claudeService.getBookRecommendations(artists);
    }
}