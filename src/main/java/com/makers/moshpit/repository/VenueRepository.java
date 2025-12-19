package com.makers.moshpit.repository;

import com.makers.moshpit.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VenueRepository extends JpaRepository<Venue, Long> {
    List<Venue> findByCountryIgnoreCase(String country);
    List<Venue> findByCityIgnoreCaseAndCountryIgnoreCase(String city, String country);
    Venue findByVenueNameIgnoreCaseAndCityIgnoreCaseAndCountryIgnoreCase(String venueName, String city, String country);
}
