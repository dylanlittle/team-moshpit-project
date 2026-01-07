package com.makers.moshpit.repository;

import com.makers.moshpit.model.ListeningEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;

public interface ListeningEventRepository extends JpaRepository<ListeningEvent, Long> {

    @Query("""
        SELECT le.playedAt
        FROM ListeningEvent le
        WHERE le.userId = :userId AND le.spotifyArtistId = :spotifyArtistId
        ORDER BY le.playedAt DESC
        """)
    Optional<Instant> findLastListenedAt(Long userId, String spotifyArtistId);
}
