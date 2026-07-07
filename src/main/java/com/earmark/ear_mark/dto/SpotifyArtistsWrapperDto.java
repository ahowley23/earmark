package com.earmark.ear_mark.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

// Wraps the /artists endpoint response:
// { "artists": [ { artist }, { artist }, ... ] }
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyArtistsWrapperDto {
    private List<SpotifyArtistDto> artists;
}