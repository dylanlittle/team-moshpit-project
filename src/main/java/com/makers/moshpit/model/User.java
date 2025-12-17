package com.makers.moshpit.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;

@Data
@Entity
@Table(name = "USERS")
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String bio;
    private Long active_artist_id;

    public User(String email, String name) {
        this.name = name;
        this.email = email;
        this.bio = "Enter Bio Here...";
        this.active_artist_id = null;
    }
}