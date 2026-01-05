package com.makers.moshpit.repository;

import com.makers.moshpit.model.Artist;
import com.makers.moshpit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    List<Artist> findByNameContainingIgnoreCase(String name);
    @Query(value = "SELECT a.* FROM artists a " +
            "INNER JOIN artist_admins aa ON a.id = aa.artist_id " +
            "WHERE aa.user_id = :userId",
            nativeQuery = true)
    List<Artist> findArtistsByUserId(@Param("userId") Long userId);

    Optional<Artist> findFirstByNameIgnoreCase(String name);

    @Query("SELECT f.artist FROM Follow f WHERE f.user = :user")
    List<Artist> findAllArtistsFollowedByUser(@Param("user") User user);
}
