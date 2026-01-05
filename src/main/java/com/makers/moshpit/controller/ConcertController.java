package com.makers.moshpit.controller;

import com.makers.moshpit.dto.ConcertForm;
import com.makers.moshpit.model.Artist;
import com.makers.moshpit.model.Concert;
import com.makers.moshpit.model.Venue;
import com.makers.moshpit.dto.ConcertForm;
import com.makers.moshpit.repository.ArtistRepository;
import com.makers.moshpit.repository.ConcertRepository;
import com.makers.moshpit.repository.VenueRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ConcertController {

    @Autowired
    ConcertRepository concertRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    VenueRepository venueRepository;

    // Render add new concert page
    @GetMapping("/artists/{artistId}/concerts/new")
    public String showConcertCreateForm(@PathVariable Long artistId, Model model) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new RuntimeException("Artist not found"));
        model.addAttribute("artist", artist);

        // Using Java Stream to process a collection of data. What it does: queries the db for all venues -> converts the result to a Stream -> for each venue, fetch the country -> remove duplicates -> sort alphabetically -> convert resulting Stream back into a List
        List<String> countries = venueRepository.findAll().stream().map(Venue::getCountry).distinct().sorted().toList();
        model.addAttribute("countries", countries);

        // Only add new Concert if not already in model from RedirectView
        if (!model.containsAttribute("concertForm")) {
            model.addAttribute("concertForm", new ConcertForm());
        }

        return "/concerts/create";
    }

    // get cities for a country
    @GetMapping("/venues/cities")
    @ResponseBody
    public List<String> getCitiesByCountry(@RequestParam String country) {
        return venueRepository.findByCountryIgnoreCase(country).stream()
                .map(Venue::getCity)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // get venues for a city and country
    @GetMapping("/venues/by-location")
    @ResponseBody
    public List<String> getVenuesByLocation(@RequestParam String country,
                                              @RequestParam String city) {
        return venueRepository.findByCityIgnoreCaseAndCountryIgnoreCase(city, country)
                .stream()
                .map(Venue::getVenueName)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // get address for a venue
    @GetMapping("/venue-address/by-location")
    @ResponseBody
    public ResponseEntity<String> getVenueAddressByLocation(@RequestParam String country,
                                            @RequestParam String city, @RequestParam String venueName) {
        Venue venue = venueRepository.findByVenueNameIgnoreCaseAndCityIgnoreCaseAndCountryIgnoreCase(venueName, city, country);

        if (venue == null) {
            return ResponseEntity.notFound().build(); // 404 → venue not in DB
        }

        return ResponseEntity.ok(venue.getAddress()); // 200 → autofill
    }

    @PostMapping("/artists/{artistId}/concerts")
    public RedirectView create(@PathVariable Long artistId, @ModelAttribute ConcertForm concertForm){

        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        // 1. Find or create venue
        Venue venue = venueRepository
                .findByVenueNameAndCityAndCountry(
                        concertForm.getVenueName(),
                        concertForm.getCity(),
                        concertForm.getCountry()
                )
                .orElseGet(() -> {
                    Venue v = new Venue(
                            concertForm.getVenueName(),
                            concertForm.getCity(),
                            concertForm.getCountry(),
                            concertForm.getAddress()
                    );
                    return venueRepository.save(v);
                });

        // 2. Create concert
        Concert concert = new Concert(
                concertForm.getConcertDate(),
                concertForm.getStartTime(),
                venue,
                artist
        );

        concertRepository.save(concert);

        return new RedirectView("/artists/" + artistId);
    }
}
