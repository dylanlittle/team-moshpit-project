package com.makers.moshpit.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@Table(name = "CONCERTS")
public class Concert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "concert_date", nullable = false)
    private LocalDate concertDate;


    @ManyToOne
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;

    public Concert(LocalDate concertDate, Venue venue, Artist artist) {
        this.concertDate = concertDate;
        this.venue = venue;
        this.artist = artist;
    }
}
