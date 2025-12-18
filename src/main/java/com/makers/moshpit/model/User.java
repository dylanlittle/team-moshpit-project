package com.makers.moshpit.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Please provide a valid name.")
    private String name;
    @NotBlank(message = "Please provide a valid username.")
    private String username;
    private String email;
    private String bio;
    private Long active_artist_id;
    @NotBlank(message = "Please provide a valid location. For example: London, UK")
    private String location;

    public User(String email) {
        this.name = "";
        this.email = email;
        this.username = "";
        this.bio = "";
        this.location = "";
        this.active_artist_id = null;
    }
}