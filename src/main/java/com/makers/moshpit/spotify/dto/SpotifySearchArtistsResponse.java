package com.makers.moshpit.spotify.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public record SpotifySearchArtistsResponse(
        Artists artists
) {
    public record Artists(List<Item> items) {}

    public record Item(
            String id,
            String name,
            List<String> genres,
            Integer popularity,
            Followers followers,
            List<SpotifyImage> images,
            @JsonProperty("external_urls") Map<String, String> externalUrls
    ) {}

    public record Followers(Integer total) {}
}

