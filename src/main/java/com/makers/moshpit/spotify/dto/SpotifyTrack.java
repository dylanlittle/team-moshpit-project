package com.makers.moshpit.spotify.dto;

import java.util.List;

public record SpotifyTrack(
        String id,
        String name,
        SpotifyAlbum album,
        List<SpotifyArtist> artists,
        ExternalUrls external_urls
) {
    public record ExternalUrls(String spotify) {}
}
