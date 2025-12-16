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

    public Artist(String name, String bio, String genre) {
        this.name = name;
        this.bio = bio;
        this.genre = genre;
    }
}
