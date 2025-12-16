package com.makers.moshpit.repository;

import com.makers.moshpit.model.Artist;
import org.springframework.data.repository.CrudRepository;

public interface ArtistRepository extends CrudRepository<Artist, Long> {
}