package com.makers.moshpit.repository;

import com.makers.moshpit.model.Post;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByArtistIdOrderByTimestampDesc(Long artistId);
}
