package com.makers.moshpit.controller;

import com.makers.moshpit.model.*;
import com.makers.moshpit.repository.ArtistRepository;
import com.makers.moshpit.repository.FollowRepository;
import com.makers.moshpit.service.AuthService;
import com.makers.moshpit.spotify.SpotifyApiService;
import com.makers.moshpit.spotify.dto.SpotifyArtist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DiscoverController {

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    AuthService authService;

    @Autowired
    FollowRepository followRepository;

    @Autowired
    SpotifyApiService spotifyApiService;

    public DiscoverController(AuthService authService,
                              ArtistRepository artistRepository,
                              FollowRepository followRepository,
                              SpotifyApiService spotifyApiService) {
        this.authService = authService;
        this.artistRepository = artistRepository;
        this.followRepository = followRepository;
        this.spotifyApiService = spotifyApiService;
    }

    @GetMapping("/discover")
    public String discover(Model model) {

        User user = authService.getCurrentUser();

        List<Artist> all = artistRepository.findAllByOrderByNameAsc();

        Set<Long> followedArtistIds = followRepository.findAllByUser(user).stream()
                .map(followedArtist -> followedArtist.getArtist().getId())
                .collect(Collectors.toSet());

        LinkedHashMap<Long, Artist> suggestedMap = new LinkedHashMap<>();

        boolean spotifyConnected = user.getSpotifyRefreshToken() != null && !user.getSpotifyRefreshToken().isBlank();

        if (spotifyConnected) {
            List<SpotifyArtist> top = spotifyApiService.getTopArtists(user, "medium_term", 20);

            for (SpotifyArtist spotifyArtist : top) {
                Optional<Artist> match = artistRepository.findFirstByNameIgnoreCase(spotifyArtist.name());

                if (match.isPresent()) {
                    Artist matchedArtist = match.get();

                    if (!followedArtistIds.contains(matchedArtist.getId())) {
                        suggestedMap.putIfAbsent(matchedArtist.getId(), matchedArtist);
                    }
                }
            }
        }

        List<Artist> suggestedArtists = new ArrayList<>(suggestedMap.values());
        Set<Long> suggestedIds = suggestedMap.keySet();

        List<Artist> remainingArtists = all.stream()
                .filter(a -> !suggestedIds.contains(a.getId()))
                .filter(a -> !followedArtistIds.contains(a.getId()))
                .collect(Collectors.toList());

        model.addAttribute("user", user);
        model.addAttribute("spotifyConnected", spotifyConnected);
        model.addAttribute("suggestedArtists", suggestedArtists);
        model.addAttribute("allArtists", remainingArtists);
        model.addAttribute("followedArtistsIds",  followedArtistIds);

        return "discover_artists";
    }

    @PostMapping("/discover/artists/{artistId}/follow")
    public RedirectView followFromDiscover(@PathVariable Long artistId) {
        User user = authService.getCurrentUser();
        Artist artist = artistRepository.getById(artistId);

        if (followRepository.findByUserAndArtist(user, artist).isEmpty()) {
            followRepository.save(new Follow(user, artist));
        }

        return new RedirectView("/discover");
    }
}
