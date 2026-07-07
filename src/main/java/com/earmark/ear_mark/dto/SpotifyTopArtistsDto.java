package com.earmark.ear_mark.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

// This matches the outer wrapper Spotify sends back:
// {
//   "items": [ { artist }, { artist }, ... ],
//   "total": 50,
//   "limit": 5,
//   "offset": 0,
//   ...other pagination fields we don't care about
// }
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyTopArtistsDto {

    // "items" maps to the array of artists in Spotify's response
    private List<SpotifyArtistDto> items;
}