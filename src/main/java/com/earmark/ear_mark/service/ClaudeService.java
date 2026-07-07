package com.earmark.ear_mark.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.earmark.ear_mark.dto.BookRecommendationDto;
import com.earmark.ear_mark.dto.SpotifyArtistDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class ClaudeService {

    @Value("${anthropic.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.anthropic.com")
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public BookRecommendationDto getBookRecommendations(List<SpotifyArtistDto> artists) {

        String artistList = artists.stream()
                .map(SpotifyArtistDto::getName)
                .collect(Collectors.joining(", "));

        String prompt = "You are a book recommendation engine that matches music taste to books. " +
                "A user's top Spotify artists are: " + artistList + ". " +
                "Based on this music taste, recommend exactly 5 books. " +
                "Consider the mood, themes, energy, and cultural associations of these artists. " +
                "You MUST respond with ONLY a valid JSON object, no preamble, no markdown, no explanation. " +
                "The JSON must match this exact structure: " +
                "{\"recommendations\": [{\"title\": \"string\", \"author\": \"string\", " +
                "\"reasoning\": \"string\", \"genres\": [\"string\"], \"moods\": [\"string\"]}]}";

        try {
            // Build request body using Jackson — safe, no string escaping needed
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", "claude-sonnet-4-6");
            requestBody.put("max_tokens", 1500);

            ArrayNode messages = requestBody.putArray("messages");
            ObjectNode message = messages.addObject();
            message.put("role", "user");
            message.put("content", prompt);

            String requestJson = objectMapper.writeValueAsString(requestBody);

            System.out.println("Calling Claude API...");

            String response = webClient.post()
                    .uri("/v1/messages")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .header("content-type", "application/json")
                    .bodyValue(requestJson)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("Claude raw response: " + response);

            var responseNode = objectMapper.readTree(response);
            // Strip markdown code fences if Claude added them
String claudeText = responseNode
        .path("content")
        .get(0)
        .path("text")
        .asText()
        .replaceAll("```json\\s*", "")
        .replaceAll("```\\s*", "")
        .trim();
            System.out.println("Claude text: " + claudeText);

            return objectMapper.readValue(claudeText, BookRecommendationDto.class);

        } catch (Exception e) {
            System.err.println("Claude API error: " + e.getClass().getName() + " - " + e.getMessage());
            return new BookRecommendationDto();
        }
    }
}
