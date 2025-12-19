package com.makers.moshpit.controller;

import com.makers.moshpit.model.Artist;
import com.makers.moshpit.model.Concert;
import com.makers.moshpit.model.Venue;
import com.makers.moshpit.repository.ArtistRepository;
import com.makers.moshpit.repository.ConcertRepository;
import com.makers.moshpit.repository.VenueRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
        if (!model.containsAttribute("concert")) {
            model.addAttribute("concert", new Concert());
        }



        return "/concerts/create";
    }

    // AJAX endpoint to get cities for a country
    @GetMapping("/venues/cities")
    @ResponseBody
    public List<String> getCitiesByCountry(@RequestParam String country) {
        return venueRepository.findByCountryIgnoreCase(country).stream()
                .map(Venue::getCity)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @PostMapping("/artists/{artistId}/concerts")
    public RedirectView create(@PathVariable Long artistId, @Valid @ModelAttribute Concert concert){

        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new RuntimeException("Artist not found"));
        concert.setArtist(artist);
        concertRepository.save(concert);

        return new RedirectView("/artists/" + artistId);
    }
}
