package com.makers.moshpit.controller;

import com.makers.moshpit.dto.ConcertForm;
import com.makers.moshpit.model.*;
import com.makers.moshpit.repository.*;
import com.makers.moshpit.service.AuthService;
import com.makers.moshpit.service.MediaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ConcertController {

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    ConcertGoerRepository concertGoerRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private MediaService mediaService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    AuthService authService;

    // get concert
    @GetMapping("/concerts/{concertId}")
    public String getConcert(@PathVariable Long concertId, Model model) {
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new EntityNotFoundException("Concert not found"));
        Venue venue = concert.getVenue();
        User currentUser = authService.getCurrentUser();
        List<Post> posts = postRepository.findAllByConcertIdOrderByTimestampDesc(concertId);
        List<User> crowd = concertGoerRepository.findUsersByConcertId(concertId);
        boolean isGoing = concertGoerRepository.existsByUserAndConcert(currentUser, concert);
        model.addAttribute("crowd", crowd);
        model.addAttribute("posts", posts);
        model.addAttribute("concert", concert);
        model.addAttribute("venue", venue);
        model.addAttribute("isGoing", isGoing);
        model.addAttribute("currentUser", currentUser);
        return "concerts/concert_page";
    }

    // rsvp to concert
    @PostMapping("/concerts/{concertId}/rsvp")
    public ResponseEntity<Void> rsvp(@PathVariable Long concertId) {
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new EntityNotFoundException("Concert not found"));
        User currentUser = authService.getCurrentUser();
        boolean isGoing = concertGoerRepository.existsByUserAndConcert(currentUser, concert);
        if (isGoing) {
            concertGoerRepository.deleteByUserAndConcert(currentUser, concert);
        } else {
            concertGoerRepository.save(new ConcertGoer(currentUser, concert));
        }
        return ResponseEntity.ok().build();
    }

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
    public RedirectView create(@PathVariable Long artistId, @ModelAttribute ConcertForm concertForm, @RequestParam(value = "image", required = false) MultipartFile imageFile){

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


        String concertImage = null;

        // 2. If an image has been uploaded, upload to cloud and extract URL
        if (imageFile != null && !imageFile.isEmpty()) {
            if (imageFile.getSize() > (10 * 1024 * 1024)) {
                throw new RuntimeException("File too large — maximum allowed size is 10MB.");
            }

            try {
                String imageUrl = mediaService.uploadImage(imageFile);
                concertImage = imageUrl;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 3. Create concert
        Concert concert = new Concert(
                concertForm.getConcertDate(),
                concertForm.getStartTime(),
                venue,
                artist,
                concertImage
        );

        concertRepository.save(concert);

        return new RedirectView("/artists/" + artistId);
    }
}
