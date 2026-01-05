package com.makers.moshpit.controller;

import com.makers.moshpit.model.Artist;
import com.makers.moshpit.model.Follow;
import com.makers.moshpit.model.User;
import com.makers.moshpit.repository.ArtistRepository;
import com.makers.moshpit.repository.FollowRepository;
import com.makers.moshpit.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@Controller
public class FollowController {

    @Autowired
    AuthService authService;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    FollowRepository followRepository;

    @PostMapping("/artists/{artistId}/follow")
    public RedirectView followArtist(@PathVariable Long artistId) {

        User user = authService.getCurrentUser();
        Artist artist = artistRepository.getById(artistId);

        boolean alreadyFollowing = followRepository.findByUserAndArtist(user, artist).isPresent();

        if (!alreadyFollowing) {
            Follow follow = new Follow(user, artist);
            followRepository.save(follow);
        }

        return new RedirectView("/artists/" + artistId);
    }

    @PostMapping("/artists/{artistId}/unfollow")
    public RedirectView unfollowArtist(@PathVariable Long artistId) {

        User user = authService.getCurrentUser();
        Artist artist = artistRepository.getById(artistId);

        followRepository.findByUserAndArtist(user, artist)
                .ifPresent(followRepository::delete);

        return new RedirectView("/artists/" + artistId);
    }
}
