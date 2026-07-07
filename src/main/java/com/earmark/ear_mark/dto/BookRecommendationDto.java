package com.earmark.ear_mark.dto;

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
}