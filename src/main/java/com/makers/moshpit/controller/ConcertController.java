package com.makers.moshpit.controller;

import com.makers.moshpit.dto.ConcertForm;
import com.makers.moshpit.dto.LineupForm;
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

import javax.sound.sampled.Line;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ConcertController {

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ConcertGoerRepository concertGoerRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private LineupArtistRepository lineupArtistRepository;

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
        List<Artist> lineup = lineupArtistRepository.findArtistsByConcertId(concertId);
        List<Post> mediaPosts = posts.stream()
                .filter(post -> "image".equals(post.getMediaType()) || "video".equals(post.getMediaType()))
                .collect(Collectors.toList());
        model.addAttribute("mediaPosts", mediaPosts);
        model.addAttribute("crowd", crowd);
        model.addAttribute("posts", posts);
        model.addAttribute("concert", concert);
        model.addAttribute("venue", venue);
        model.addAttribute("isGoing", isGoing);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("lineup", lineup);
        if (!model.containsAttribute("post")) {
            model.addAttribute("post", new Post());
        }
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

    // create new concert
    @PostMapping("/artists/{artistId}/concerts")
    public RedirectView create(@PathVariable Long artistId, @ModelAttribute ConcertForm concertForm, @RequestParam(value = "image", required = false) MultipartFile imageFile){

        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        // Find or create venue
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

        // If an image has been uploaded, upload to cloud and extract URL
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

        String concertName;
        if (concertForm.getConcertName().isBlank()) {
            concertName = artist.getName() + " at " + venue.getVenueName();
        } else {
            concertName = concertForm.getConcertName();
        }

        // Create concert
        Concert concert = new Concert(
                concertName,
                concertForm.getConcertDate(),
                concertForm.getStartTime(),
                venue,
                artist,
                concertImage
        );

        concertRepository.save(concert);

        // Add artist to LineupArtist table
        LineupArtist lineupArtist = new LineupArtist(artist, concert);
        lineupArtistRepository.save(lineupArtist);

        return new RedirectView("/concerts/" + concert.getId() + "/lineup/new");
    }

    // render add lineup form
    @GetMapping("/concerts/{concertId}/lineup/new")
    public String showLineupForm(@PathVariable Long concertId, Model model) {

        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new RuntimeException("Concert not found"));

        model.addAttribute("concert", concert);
        model.addAttribute("artists", artistRepository.findAll());
        model.addAttribute("lineupForm", new LineupForm());

        return "concerts/lineup_form";
    }

    // add lineup to concert
    @PostMapping("/concerts/{concertId}/lineup")
    public RedirectView addLineup(
            @PathVariable Long concertId,
            @ModelAttribute LineupForm lineupForm
    ) {

        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new RuntimeException("Concert not found"));

        for (Long artistId : lineupForm.getArtistIds()) {
            if (artistId == null) continue; // skip empty selects

            Artist artist = artistRepository.findById(artistId)
                    .orElseThrow(() -> new RuntimeException("Artist not found"));

            if (lineupArtistRepository.existsByArtistAndConcert(artist, concert)) {
                continue;
            }

            LineupArtist lineupArtist = new LineupArtist(artist, concert);
            lineupArtistRepository.save(lineupArtist);
        }

        return new RedirectView("/concerts/" + concertId);
    }
}
