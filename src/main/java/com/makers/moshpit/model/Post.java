package com.makers.moshpit.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
@Table(name = "POSTS")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(insertable = false, updatable = false)
    private Instant timestamp;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;

    // If posting as a user, artist_id will default to null
    public Post(String content) {
        this.content = content;
        this.artist = null;
    }

    // If posting as an artist, user_id will default to null
    public Post(String content, Artist artist) {
        this.content = content;
        this.artist = artist;
    }
}