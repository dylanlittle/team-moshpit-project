package com.makers.moshpit.repository;

import com.makers.moshpit.model.ArtistAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistAdminRepository extends JpaRepository<ArtistAdmin, Long> {
    boolean existsByArtistIdAndUserId(Long artistId, Long userId);
}
