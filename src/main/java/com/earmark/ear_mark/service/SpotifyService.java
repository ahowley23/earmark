package com.earmark.ear_mark.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.earmark.ear_mark.dto.SpotifyArtistDto;
import com.earmark.ear_mark.dto.SpotifyTopArtistsDto;

@Service
public class SpotifyService {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.spotify.com/v1")
            .build();

    // Returns top artists with names and IDs
    // Note: genres and popularity not available for dev-mode apps
    // as of Spotify's Nov 2024 API restrictions — handled at taste
    // profile layer using artist names directly
    public List<SpotifyArtistDto> getTopArtists(String accessToken, String timeRange) {

        try {
            SpotifyTopArtistsDto response = webClient.get()
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
            // Log the status and message for debugging
            System.err.println("Spotify API error: " + e.getStatusCode() + " - " + e.getMessage());
            return List.of();
        }
    }
}