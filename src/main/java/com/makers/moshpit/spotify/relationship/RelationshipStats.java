package com.makers.moshpit.spotify.relationship;

import java.time.Instant;
import java.util.List;

public record RelationshipStats(
        boolean connected,
        boolean inTopArtists,
        Integer topArtistRank,
        List<TopTrack> topTracksByArtist,
        Integer savedTracksCount,
        Instant lastListenedAt
) {
    public record TopTrack(
            String trackId,
            String name,
            String spotifyUrl,
            String albumImageUrl
    ) {}
}

