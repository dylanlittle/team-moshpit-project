package com.makers.moshpit.controller;

import com.makers.moshpit.model.Artist;
import com.makers.moshpit.model.Concert;
import com.makers.moshpit.model.Post;
import com.makers.moshpit.model.User;
import com.makers.moshpit.repository.ArtistRepository;
import com.makers.moshpit.repository.ConcertRepository;
import com.makers.moshpit.repository.FollowRepository;
import com.makers.moshpit.repository.PostRepository;
import com.makers.moshpit.service.AuthService;
import com.makers.moshpit.spotify.CurrentUserService;
import com.makers.moshpit.spotify.SpotifyApiService;
import com.makers.moshpit.spotify.dto.SpotifyArtist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DiscoverController {

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    CurrentUserService currentUserService;

    @Autowired
    FollowRepository followRepository;

    @Autowired
    SpotifyApiService spotifyApiService;

    public DiscoverController(CurrentUserService currentUserService,
                              ArtistRepository artistRepository,
                              FollowRepository followRepository,
                              SpotifyApiService spotifyApiService) {
        this.currentUserService = currentUserService;
        this.artistRepository = artistRepository;
        this.followRepository = followRepository;
        this.spotifyApiService = spotifyApiService;
    }

    @GetMapping("/discover")
    public String discover(Model model,
                           @AuthenticationPrincipal OAuth2User principal) {

        User user = currentUserService.getOrCreateFromPrincipal(principal);

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
                .collect(Collectors.toList());

        model.addAttribute("user", user);
        model.addAttribute("spotifyConnected", spotifyConnected);
        model.addAttribute("suggestedArtists", suggestedArtists);
        model.addAttribute("allArtists", remainingArtists);
        model.addAttribute("followedArtistsIds",  followedArtistIds);

        return "discover_artists";
    }
}
