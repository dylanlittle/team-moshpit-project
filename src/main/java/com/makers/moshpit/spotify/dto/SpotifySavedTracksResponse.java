package com.makers.moshpit.spotify.dto;

import java.util.List;

public record SpotifySavedTracksResponse(
        List<SavedTrackItem> items,
        Integer limit,
        Integer offset,
        Integer total,
        String next,
        String previous
) {
    public record SavedTrackItem(
            String added_at,
            SpotifyTrack track
    ) {}
}

