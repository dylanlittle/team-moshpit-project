package com.makers.moshpit.repository;

import com.makers.moshpit.model.Artist;
import com.makers.moshpit.model.Concert;
import com.makers.moshpit.model.LineupArtist;
import com.makers.moshpit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LineupArtistRepository extends JpaRepository<LineupArtist, Long> {
    boolean existsByArtistAndConcert(Artist artist, Concert concert);
    @Query("SELECT cg.artist FROM LineupArtist cg WHERE cg.concert.id = :concertId")
    List<Artist> findArtistsByConcertId(@Param("concertId") Long concertId);
}
