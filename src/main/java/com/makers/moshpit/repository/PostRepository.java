package com.makers.moshpit.repository;

import com.makers.moshpit.model.Post;
import com.makers.moshpit.model.User;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByArtistIdOrderByTimestampDesc(Long artistId);
    Iterable<Post> findAllByUserIdOrderByTimestampDesc(Long userId);
    List<Post> findAllByConcertIdOrderByTimestampDesc(Long concertId);

    @Query("SELECT p FROM Post p JOIN Follow f ON p.artist = f.artist WHERE f.user = :user ORDER BY p.timestamp DESC")
    List<Post> findPostsByFollowedArtists(@Param("user") User user);
}
