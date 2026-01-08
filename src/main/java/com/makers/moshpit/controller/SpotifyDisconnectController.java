package com.makers.moshpit.controller;

import com.makers.moshpit.model.User;
import com.makers.moshpit.repository.UserRepository;
import com.makers.moshpit.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class SpotifyDisconnectController {

    @Autowired
    AuthService authService;

    @Autowired
    private UserRepository userRepository;

    public SpotifyDisconnectController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/spotify/disconnect")
    public RedirectView disconnect() {
        User user = authService.getCurrentUser();

        user.setSpotifyUserId(null);
        user.setSpotifyAccessToken(null);
        user.setSpotifyRefreshToken(null);
        user.setSpotifyTokenExpiresAt(null);

        userRepository.save(user);

        return new RedirectView("/user?spotifyDisconnected=true");
    }
}

