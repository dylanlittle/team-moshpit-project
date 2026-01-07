package com.makers.moshpit.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "ARTISTS")
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String bio;

    @Column(nullable = false)
    private String genre;

    @Column(nullable = false)
    private boolean verified = false;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    private String spotifyArtistId;

    public Artist(String name, String bio, String genre, Long createdBy ) {
        this.name = name;
        this.bio = bio;
        this.genre = genre;
        this.createdBy = createdBy;
        this.verified = false;
    }
}
