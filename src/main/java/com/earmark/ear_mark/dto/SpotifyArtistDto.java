package com.earmark.ear_mark.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

// JsonIgnoreProperties tells Jackson to ignore any fields Spotify sends
// that we haven't mapped here — without this, unknown fields cause errors
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyArtistDto {
   private String id;
    private String name;
    private List<String> genres;
    private Integer popularity;
}