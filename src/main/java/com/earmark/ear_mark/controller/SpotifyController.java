package com.earmark.ear_mark.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.earmark.ear_mark.dto.BookRecommendationDto;
import com.earmark.ear_mark.service.RecommendationService;
import com.earmark.ear_mark.service.SpotifyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/spotify")
@RequiredArgsConstructor
public class SpotifyController {

    private final SpotifyService spotifyService;
    private final RecommendationService recommendationService;

    @GetMapping("/connect")
    public ResponseEntity<String> connect(Principal principal) {
        String authUrl = spotifyService.buildAuthorizationUrl(principal.getName());
        return ResponseEntity.ok(authUrl);
    }

    @GetMapping("/callback")
    public ResponseEntity<String> callback(
            @RequestParam String code,
            @RequestParam String state) {
        spotifyService.handleCallback(code, state);
        return ResponseEntity.ok("Spotify connected successfully");
    }

    @GetMapping("/recommendations")
        public ResponseEntity<List<BookRecommendationDto.RecommendationResponse>> getRecommendations(Principal principal) {
                return ResponseEntity.ok(
                recommendationService.getOrGenerateRecommendations(principal.getName())
    );
}
}