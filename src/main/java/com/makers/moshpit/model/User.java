package com.makers.moshpit.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "USERS")
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String username;
    private String email;
    private String bio;
    private Long active_artist_id;
    private String avatar;
    private String location;
    private boolean shareSpotifyTopArtists;

    @Column(name = "auth0_sub", unique = true)
    private String auth0Sub;

    @Column(name = "spotify_user_id")
    private String spotifyUserId;

    @Column(name = "spotify_access_token", columnDefinition = "TEXT")
    private String spotifyAccessToken;

    @Column(name = "spotify_refresh_token", columnDefinition = "TEXT")
    private String spotifyRefreshToken;

    @Column(name = "spotify_token_expires_at")
    private LocalDateTime spotifyTokenExpiresAt;

    public User(String email) {
        this.name = "";
        this.email = email;
        this.username = "";
        this.bio = "";
        this.location = "";
        this.active_artist_id = null;
    }
}