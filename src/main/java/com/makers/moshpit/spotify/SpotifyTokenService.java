package com.makers.moshpit.spotify;

import com.makers.moshpit.model.User;
import com.makers.moshpit.repository.UserRepository;
import com.makers.moshpit.spotify.dto.SpotifyTokenResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class SpotifyTokenService {

    private final SpotifyProperties props;
    private final UserRepository userRepository;
    private final RestClient restClient = RestClient.create();

    public SpotifyTokenService(SpotifyProperties props, UserRepository userRepository) {
        this.props = props;
        this.userRepository = userRepository;
    }

    public void exchangeCodeAndStoreTokens(User user, String code) {
        String basic = Base64.getEncoder().encodeToString(
                (props.getClientId() + ":" + props.getClientSecret()).getBytes(StandardCharsets.UTF_8)
        );

        String body = "grant_type=authorization_code"
                + "&code=" + UriUtils.encode(code, StandardCharsets.UTF_8)
                + "&redirect_uri=" + UriUtils.encode(props.getRedirectUri(), StandardCharsets.UTF_8);

        SpotifyTokenResponse res = restClient.post()
                .uri(props.getTokenUrl())
                .header(HttpHeaders.AUTHORIZATION, "Basic " + basic)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(SpotifyTokenResponse.class);

        user.setSpotifyAccessToken(res.accessToken());

        if (res.refreshToken() != null && !res.refreshToken().isBlank()) {
            user.setSpotifyRefreshToken(res.refreshToken());
        }

        user.setSpotifyTokenExpiresAt(LocalDateTime.now().plusSeconds(res.expiresIn()));
        userRepository.save(user);
    }

    public String getValidAccessToken(User user) {
        if (user.getSpotifyAccessToken() != null
                && user.getSpotifyTokenExpiresAt() != null
                && user.getSpotifyTokenExpiresAt().isAfter(LocalDateTime.now().plusSeconds(30))) {
            return user.getSpotifyAccessToken();
        }
        return refreshAccessToken(user);
    }

    private String refreshAccessToken(User user) {
        if (user.getSpotifyRefreshToken() == null) {
            throw new RuntimeException("Spotify not connected");
        }

        String basic = Base64.getEncoder().encodeToString(
                (props.getClientId() + ":" + props.getClientSecret()).getBytes(StandardCharsets.UTF_8)
        );

        String body = "grant_type=refresh_token"
                + "&refresh_token=" + UriUtils.encode(user.getSpotifyRefreshToken(), StandardCharsets.UTF_8);

        SpotifyTokenResponse res = restClient.post()
                .uri(props.getTokenUrl())
                .header(HttpHeaders.AUTHORIZATION, "Basic " + basic)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(SpotifyTokenResponse.class);

        user.setSpotifyAccessToken(res.accessToken());
        user.setSpotifyTokenExpiresAt(LocalDateTime.now().plusSeconds(res.expiresIn()));
        userRepository.save(user);

        return user.getSpotifyAccessToken();
    }
}

