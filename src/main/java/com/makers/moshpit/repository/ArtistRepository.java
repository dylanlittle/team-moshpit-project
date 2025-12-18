package com.makers.moshpit.repository;

import com.makers.moshpit.model.Artist;
import com.makers.moshpit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtistRepository extends JpaRepository<Artist, Long> {

}