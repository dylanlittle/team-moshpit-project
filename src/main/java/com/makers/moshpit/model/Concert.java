package com.makers.moshpit.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "CONCERTS")
public class Concert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String concertName;

    @Column(name = "concert_date", nullable = false)
    private LocalDate concertDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @ManyToOne
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @Column(name = "image")
    private String image;

    public Concert(String concertName, LocalDate concertDate, LocalTime startTime, Venue venue, Artist artist, String image) {
        this.concertName = concertName;
        this.concertDate = concertDate;
        this.startTime = startTime;
        this.venue = venue;
        this.artist = artist;
        this.image = image;
    }
}
