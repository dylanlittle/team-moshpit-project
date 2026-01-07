package com.makers.moshpit.repository;

import com.makers.moshpit.model.Artist;
import com.makers.moshpit.model.Concert;
import com.makers.moshpit.model.LineupArtist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineupArtistRepository extends JpaRepository<LineupArtist, Long> {
    boolean existsByArtistAndConcert(Artist artist, Concert concert);
}
