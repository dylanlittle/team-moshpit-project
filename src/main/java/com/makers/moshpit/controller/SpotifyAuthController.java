package com.makers.moshpit.controller;

import com.makers.moshpit.model.User;
import com.makers.moshpit.spotify.CurrentUserService;
import com.makers.moshpit.spotify.SpotifyProperties;
import com.makers.moshpit.spotify.SpotifyTokenService;
import jakarta.persistence.Column;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Controller
public class SpotifyAuthController {

    @Autowired
    private SpotifyProperties spotifyProperties;

    @Autowired
    private SpotifyTokenService tokenService;

    @Autowired
    private CurrentUserService currentUserService;

    public SpotifyAuthController(SpotifyProperties spotifyProperties,
                                 SpotifyTokenService tokenService,
                                 CurrentUserService currentUserService) {
        this.spotifyProperties = spotifyProperties;
        this.tokenService = tokenService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/spotify/connect")
    public RedirectView connect(HttpSession session) {
        String state = UUID.randomUUID().toString();
        session.setAttribute("spotify_oauth_state", state);

        String scope = "user-top-read";

        String url = UriComponentsBuilder
                .fromUriString(spotifyProperties.getAuthorizeUrl())
                .queryParam("response_type", "code")
                .queryParam("client_id", spotifyProperties.getClientId())
                .queryParam("scope", scope)
                .queryParam("redirect_uri", spotifyProperties.getRedirectUri())
                .queryParam("state", state)
                .queryParam("show_dialog", "true")
                .queryParam("prompt", "login")
                .build(true)
                .toUriString();

        return new RedirectView(url);
    }

    @GetMapping("/spotify/callback")
    public RedirectView callback(@RequestParam(required = false) String code,
                                 @RequestParam(required = false) String state,
                                 @RequestParam(required = false) String error,
                                 HttpSession session,
                                 @AuthenticationPrincipal OAuth2User principal) {

        if (error != null) {
            return new RedirectView("/user?spotifyError=" + UriUtils.encode(error, StandardCharsets.UTF_8));
        }

        String expectedState = (String) session.getAttribute("spotify_oauth_state");
        session.removeAttribute("spotify_oauth_state");
        if (expectedState == null || !expectedState.equals(state)) {
            return new RedirectView("/user?spotifyError=state_mismatch");
        }

        User user = currentUserService.getOrCreateFromPrincipal(principal);
        tokenService.exchangeCodeAndStoreTokens(user, code);

        return new RedirectView("/user?spotifyConnected=true");
    }
}
