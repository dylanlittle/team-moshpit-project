package com.makers.moshpit.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "lineup_artists",
        uniqueConstraints = @UniqueConstraint(columnNames = {"artist_id", "concert_id"}))
public class LineupArtist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @ManyToOne(optional = false)
    @JoinColumn(name = "concert_id")
    private Concert concert;

    public LineupArtist(Artist artist, Concert concert) {
        this.artist = artist;
        this.concert = concert;
    }
}

