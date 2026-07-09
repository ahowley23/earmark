package com.earmark.ear_mark.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookRecommendationDto {

    private List<Recommendation> recommendations;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Recommendation {

        private String title;
        private String author;
        private String reasoning;
        private List<String> genres;
        private List<String> moods;
    }

    @Data
public static class RecommendationResponse {
    private Long id;
    private String bookTitle;
    private String bookAuthor;
    private String reasoning;
    private String engine;
    private LocalDateTime createdAt;
}
}