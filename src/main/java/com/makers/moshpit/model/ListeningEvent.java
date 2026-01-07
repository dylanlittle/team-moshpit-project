package com.makers.moshpit.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "listening_events")
public class ListeningEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable=false)
    private Long userId;

    @Column(name="spotify_artist_id", nullable=false, length=64)
    private String spotifyArtistId;

    @Column(name="spotify_track_id", length=64)
    private String spotifyTrackId;

    @Column(name="played_at", nullable=false)
    private Instant playedAt = Instant.now();

    public ListeningEvent() {}

    public ListeningEvent(Long userId, String spotifyArtistId, String spotifyTrackId) {
        this.userId = userId;
        this.spotifyArtistId = spotifyArtistId;
        this.spotifyTrackId = spotifyTrackId;
        this.playedAt = Instant.now();
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getSpotifyArtistId() { return spotifyArtistId; }
    public String getSpotifyTrackId() { return spotifyTrackId; }
    public Instant getPlayedAt() { return playedAt; }
}

