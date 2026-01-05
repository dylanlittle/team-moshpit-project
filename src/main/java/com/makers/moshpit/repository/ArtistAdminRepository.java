package com.makers.moshpit.repository;

import com.makers.moshpit.model.ArtistAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArtistAdminRepository extends JpaRepository<ArtistAdmin, Long> {
    boolean existsByArtistIdAndUserId(Long artistId, Long userId);
    @Query("""
        select aa
        from ArtistAdmin aa
        join fetch aa.user
        where aa.artist.id = :artistId
        """)
    List<ArtistAdmin> findAdminsByArtistId(@Param("artistId") Long artistId);
}
