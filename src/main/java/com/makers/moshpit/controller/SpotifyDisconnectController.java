package com.makers.moshpit.controller;

import com.makers.moshpit.model.User;
import com.makers.moshpit.repository.UserRepository;
import com.makers.moshpit.service.AuthService;
import com.makers.moshpit.spotify.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class SpotifyDisconnectController {

    @Autowired
    CurrentUserService currentUserService;

    @Autowired
    private UserRepository userRepository;

    public SpotifyDisconnectController(CurrentUserService currentUserService, UserRepository userRepository) {
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
    }

    @PostMapping("/spotify/disconnect")
    public RedirectView disconnect(@AuthenticationPrincipal OAuth2User principal) {
        User user = currentUserService.getOrCreateFromPrincipal(principal);

        user.setSpotifyUserId(null);
        user.setSpotifyAccessToken(null);
        user.setSpotifyRefreshToken(null);
        user.setSpotifyTokenExpiresAt(null);

        userRepository.save(user);

        return new RedirectView("/user?spotifyDisconnected=true");
    }
}

