package com.makers.moshpit.repository;

import com.makers.moshpit.model.Artist;
import com.makers.moshpit.model.Follow;
import com.makers.moshpit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface
FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByUserAndArtist(User user, Artist artist);

    List<Follow> findAllByUser(User user);

    boolean existsByUserAndArtist(User user, Artist artist);
}
