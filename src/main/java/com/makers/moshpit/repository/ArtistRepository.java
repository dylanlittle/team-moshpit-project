package com.makers.moshpit.repository;

import com.makers.moshpit.model.Artist;
import com.makers.moshpit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
    List<Artist> findByNameContainingIgnoreCase(String name);
    Optional<Artist> findFirstByNameIgnoreCase(String name);

    @Query("SELECT f.artist FROM Follow f WHERE f.user = :user")
    List<Artist> findAllArtistsFollowedByUser(@Param("user") User user);
}