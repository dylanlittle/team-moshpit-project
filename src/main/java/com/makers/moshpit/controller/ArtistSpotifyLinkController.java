package com.makers.moshpit.controller;

import com.makers.moshpit.model.Artist;
import com.makers.moshpit.model.ArtistAdmin;
import com.makers.moshpit.repository.ArtistAdminRepository;
import com.makers.moshpit.repository.ArtistRepository;
import com.makers.moshpit.service.AuthService;
import com.makers.moshpit.spotify.SpotifyApiService;
import com.makers.moshpit.spotify.dto.SpotifySearchArtistsResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ArtistSpotifyLinkController {

    private final ArtistRepository artistRepository;
    private final SpotifyApiService spotifyApiService;
    private final AuthService authService;
    private final ArtistAdminRepository artistAdminRepository;

    public ArtistSpotifyLinkController(ArtistRepository artistRepository,
                                       SpotifyApiService spotifyApiService,
                                       AuthService authService,
                                       ArtistAdminRepository artistAdminRepository) {
        this.artistRepository = artistRepository;
        this.spotifyApiService = spotifyApiService;
        this.authService = authService;
        this.artistAdminRepository = artistAdminRepository;
    }

    @GetMapping("/artists/{id}/spotify/link")
    public String linkSpotifyPage(@PathVariable Long id,
                                  @RequestParam(value = "q", required = false) String q,
                                  Model model) {

        var user = authService.getCurrentUser();

        Artist artist = artistRepository.findById(id).orElseThrow();

        List<SpotifySearchArtistsResponse.Item> results =
                (q == null || q.isBlank()) ? List.of() : spotifyApiService.searchArtistsApp(q, 10);

        model.addAttribute("artist", artist);
        model.addAttribute("query", q == null ? "" : q);
        model.addAttribute("results", results);

        return "link_spotify";
    }

    @PostMapping("/artists/{id}/spotify/link")
    public String linkSpotifyArtist(@PathVariable Long id,
                                    @RequestParam("spotifyArtistId") String spotifyArtistId) {

        var user = authService.getCurrentUser();

        Artist artist = artistRepository.findById(id).orElseThrow();
        artist.setSpotifyArtistId(spotifyArtistId);
        artistRepository.save(artist);

        return "redirect:/artists/" + id;
    }

    @PostMapping("/artists/{id}/spotify/unlink")
    public String unlinkSpotifyArtist(@PathVariable Long id) {

        var user = authService.getCurrentUser();

        Artist artist = artistRepository.findById(id).orElseThrow();
        artist.setSpotifyArtistId(null);
        artistRepository.save(artist);

        return "redirect:/artists/" + id;
    }
}
