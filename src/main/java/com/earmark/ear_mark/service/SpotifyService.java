package com.earmark.ear_mark.service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.earmark.ear_mark.dto.SpotifyArtistDto;
import com.earmark.ear_mark.dto.SpotifyTopArtistsDto;
import com.earmark.ear_mark.model.SpotifyCredential;
import com.earmark.ear_mark.model.User;
import com.earmark.ear_mark.repository.SpotifyCredentialRepository;
import com.earmark.ear_mark.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpotifyService {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    @Value("${spotify.redirect.uri}")
    private String redirectUri;

    private final SpotifyCredentialRepository credentialRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final WebClient spotifyApiClient = WebClient.builder()
            .baseUrl("https://api.spotify.com/v1")
            .build();

    private final WebClient spotifyAuthClient = WebClient.builder()
            .baseUrl("https://accounts.spotify.com")
            .build();

    // Step 1 — build the Spotify authorization URL
    // The frontend redirects to this URL to start the OAuth flow
    public String buildAuthorizationUrl(String state) {
        return "https://accounts.spotify.com/authorize" +
                "?client_id=" + clientId +
                "&response_type=code" +
                "&redirect_uri=" + redirectUri +
                "&scope=user-top-read" +
                "&state=" + state;
    }

    // Step 2 — exchange the authorization code for tokens
    // Called from the callback endpoint after Spotify redirects back
    public void handleCallback(String code, String email) {

        // Base64 encode client_id:client_secret for Basic Auth
        String credentials = Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes());

        try {
            // Exchange code for tokens
            String response = spotifyAuthClient.post()
                    .uri("/api/token")
                    .header("Authorization", "Basic " + credentials)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .bodyValue("grant_type=authorization_code" +
                            "&code=" + code +
                            "&redirect_uri=" + redirectUri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode tokenResponse = objectMapper.readTree(response);

            String accessToken = tokenResponse.get("access_token").asText();
            String refreshToken = tokenResponse.get("refresh_token").asText();
            int expiresIn = tokenResponse.get("expires_in").asInt();

            // Find the user and save their credentials
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Update existing credentials or create new ones
            SpotifyCredential credential = credentialRepository
                    .findByUserId(user.getId())
                    .orElse(new SpotifyCredential());

            credential.setUser(user);
            credential.setAccessToken(accessToken);
            credential.setRefreshToken(refreshToken);
            credential.setExpiresAt(LocalDateTime.now().plusSeconds(expiresIn));
            credential.setAuthorizedAt(LocalDateTime.now());

            credentialRepository.save(credential);

        } catch (Exception e) {
            throw new RuntimeException("Failed to exchange Spotify code: " + e.getMessage());
        }
    }

    // Get the stored access token for a user, refreshing if expired
    public String getValidAccessToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SpotifyCredential credential = credentialRepository
                .findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Spotify not connected"));

        // Refresh if expired or expiring within 5 minutes
        if (credential.getExpiresAt().isBefore(LocalDateTime.now().plusMinutes(5))) {
            refreshAccessToken(credential);
        }

        return credential.getAccessToken();
    }

    // Refresh an expired access token using the refresh token
    private void refreshAccessToken(SpotifyCredential credential) {
        String credentials = Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes());

        try {
            String response = spotifyAuthClient.post()
                    .uri("/api/token")
                    .header("Authorization", "Basic " + credentials)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .bodyValue("grant_type=refresh_token" +
                            "&refresh_token=" + credential.getRefreshToken())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode tokenResponse = objectMapper.readTree(response);

            credential.setAccessToken(tokenResponse.get("access_token").asText());
            credential.setExpiresAt(LocalDateTime.now()
                    .plusSeconds(tokenResponse.get("expires_in").asInt()));

            // Spotify sometimes issues a new refresh token — store it if so
            if (tokenResponse.has("refresh_token")) {
                credential.setRefreshToken(tokenResponse.get("refresh_token").asText());
            }

            credentialRepository.save(credential);

        } catch (Exception e) {
            throw new RuntimeException("Failed to refresh Spotify token: " + e.getMessage());
        }
    }

    // Get top artists using the stored token
    public List<SpotifyArtistDto> getTopArtists(String email, String timeRange) {
        String accessToken = getValidAccessToken(email);

        try {
            SpotifyTopArtistsDto response = spotifyApiClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/me/top/artists")
                            .queryParam("limit", 50)
                            .queryParam("time_range", timeRange)
                            .build()
                    )
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(SpotifyTopArtistsDto.class)
                    .block();

            if (response == null || response.getItems() == null) {
                return List.of();
            }

            return response.getItems();

        } catch (WebClientResponseException e) {
            System.err.println("Spotify API error: " + e.getStatusCode());
            return List.of();
        }
    }
}