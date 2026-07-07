package com.earmark.ear_mark.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.earmark.ear_mark.service.ClaudeService;
import com.earmark.ear_mark.service.SpotifyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/spotify")
@RequiredArgsConstructor
public class SpotifyController {

    private final SpotifyService spotifyService;
    private final ClaudeService claudeService;

    // GET /api/spotify/connect
    // Redirects the user to Spotify's authorization page
    // Requires a valid JWT — we need to know who is connecting
    @GetMapping("/connect")
    public ResponseEntity<String> connect(Principal principal) {
        // Use the email as the state parameter so we know who
        // is coming back when Spotify redirects to /callback
        String authUrl = spotifyService.buildAuthorizationUrl(principal.getName());
        return ResponseEntity.ok(authUrl);
    }

    // GET /api/spotify/callback
    // Spotify redirects here after the user approves
    // state contains the user's email from the connect step
    @GetMapping("/callback")
    public ResponseEntity<String> callback(
            @RequestParam String code,
            @RequestParam String state) {

        // state = the email we passed in the connect step
        // this ties the Spotify tokens to the right user
        spotifyService.handleCallback(code, state);
        return ResponseEntity.ok("Spotify connected successfully");
    }

    // GET /api/spotify/recommendations
    // Gets top artists from Spotify and returns book recommendations
    // Requires a valid JWT
    @GetMapping("/recommendations")
    public ResponseEntity<Object> getRecommendations(Principal principal) {
        String email = principal.getName();
        var artists = spotifyService.getTopArtists(email, "medium_term");
        var recommendations = claudeService.getBookRecommendations(artists);
        return ResponseEntity.ok(recommendations);
    }
}