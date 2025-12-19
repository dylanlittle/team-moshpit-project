package com.makers.moshpit.repository;

import com.makers.moshpit.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VenueRepository extends JpaRepository<Venue, Long> {
    List<Venue> findByCountryIgnoreCase(String country);
}
