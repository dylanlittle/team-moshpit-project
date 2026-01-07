package com.makers.moshpit.spotify.relationship;

import com.makers.moshpit.model.Artist;
import com.makers.moshpit.model.User;
import com.makers.moshpit.repository.ListeningEventRepository;
import com.makers.moshpit.spotify.SpotifyApiService;
import com.makers.moshpit.spotify.dto.SpotifyArtist;
import com.makers.moshpit.spotify.dto.SpotifySavedTracksResponse;
import com.makers.moshpit.spotify.dto.SpotifyTrack;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class RelationshipService {

    private final SpotifyApiService spotifyApi;
    private final ListeningEventRepository listeningRepo;

    public RelationshipService(SpotifyApiService spotifyApi, ListeningEventRepository listeningRepo) {
        this.spotifyApi = spotifyApi;
        this.listeningRepo = listeningRepo;
    }

    public RelationshipStats build(User user, Artist artist, String timeRange) {
        if (user == null || user.getSpotifyRefreshToken() == null) {
            return new RelationshipStats(false, false, null, List.of(), null, null);
        }

        String spotifyArtistId = artist.getSpotifyArtistId();
        if (spotifyArtistId == null || spotifyArtistId.isBlank()) {
            // Spotify not mapped for this seeded artist yet.
            return new RelationshipStats(true, false, null, List.of(), null, null);
        }

        String range = normalizeTimeRange(timeRange);

        List<SpotifyArtist> topArtists = spotifyApi.getTopArtists(user, range, 50);
        boolean inTop = false;
        Integer rank = null;

        for (int i = 0; i < topArtists.size(); i++) {
            if (spotifyArtistId.equals(topArtists.get(i).id())) { // assuming SpotifyArtist is a record
                inTop = true;
                rank = i + 1;
                break;
            }
        }

        List<SpotifyTrack> topTracks = spotifyApi.getTopTracks(user, range, 50);
        List<RelationshipStats.TopTrack> tracksByArtist = new ArrayList<>();

        for (SpotifyTrack t : topTracks) {
            if (t == null || t.artists() == null) continue;

            boolean match = t.artists().stream().anyMatch(a -> spotifyArtistId.equals(a.id()));
            if (!match) continue;

            String img = null;
            if (t.album() != null && t.album().images() != null && !t.album().images().isEmpty()) {
                img = t.album().images().get(0).url();
            }

            String url = (t.external_urls() != null) ? t.external_urls().spotify() : null;

            tracksByArtist.add(new RelationshipStats.TopTrack(t.id(), t.name(), url, img));
            if (tracksByArtist.size() >= 5) break;
        }

        Integer savedCount = countSavedTracksByArtist(user, spotifyArtistId, 300);

        Instant lastListened = listeningRepo.findLastListenedAt(user.getId(), spotifyArtistId).orElse(null);

        return new RelationshipStats(true, inTop, rank, tracksByArtist, savedCount, lastListened);
    }

    private Integer countSavedTracksByArtist(User user, String spotifyArtistId, int maxToScan) {
        int limit = 50;
        int offset = 0;
        int scanned = 0;
        int count = 0;

        while (scanned < maxToScan) {
            SpotifySavedTracksResponse page = spotifyApi.getSavedTracksPage(user, limit, offset);
            if (page == null || page.items() == null || page.items().isEmpty()) break;

            for (SpotifySavedTracksResponse.SavedTrackItem item : page.items()) {
                scanned++;
                if (item == null || item.track() == null || item.track().artists() == null) continue;

                boolean match = item.track().artists().stream().anyMatch(a -> spotifyArtistId.equals(a.id()));
                if (match) count++;

                if (scanned >= maxToScan) break;
            }

            offset += limit;
            if (page.total() == null || offset >= page.total()) break;
        }

        return count;
    }

    private String normalizeTimeRange(String timeRange) {
        if (timeRange == null) return "medium_term";
        return switch (timeRange) {
            case "short_term", "medium_term", "long_term" -> timeRange;
            default -> "medium_term";
        };
    }
}
