package com.makers.moshpit.spotify.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record SpotifyArtist(
        String id,
        String name,
        List<String> genres,
        Integer popularity,
        List<SpotifyImage> images,
        @JsonProperty("external_urls") Map<String, String> externalUrls
) {}

