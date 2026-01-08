package com.makers.moshpit.spotify.dto;

import java.util.List;

public record SpotifyTopTracksResponse(
        List<SpotifyTrack> items,
        Integer limit,
        Integer offset,
        Integer total,
        String next,
        String previous
) {}
