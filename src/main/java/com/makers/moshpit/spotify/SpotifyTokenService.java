package com.makers.moshpit.spotify;

import com.makers.moshpit.model.User;
import com.makers.moshpit.repository.UserRepository;
import com.makers.moshpit.spotify.dto.SpotifyTokenResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriUtils;
import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;


import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class SpotifyTokenService {

    private final SpotifyProperties props;
    private final UserRepository userRepository;
    private final RestClient restClient = RestClient.create();
    private volatile String appAccessToken;
    private volatile Instant appTokenExpiresAt;
    private final ReentrantLock appTokenLock = new ReentrantLock();

    public String getAppAccessToken() {
        if (appAccessToken != null && appTokenExpiresAt != null && Instant.now().isBefore(appTokenExpiresAt.minusSeconds(30))) {
            return appAccessToken;
        }

        appTokenLock.lock();
        try {
            // double-check after acquiring lock
            if (appAccessToken != null && appTokenExpiresAt != null && Instant.now().isBefore(appTokenExpiresAt.minusSeconds(30))) {
                return appAccessToken;
            }

            String basic = Base64.getEncoder().encodeToString(
                    (props.getClientId() + ":" + props.getClientSecret()).getBytes(StandardCharsets.UTF_8)
            );

            String body = "grant_type=client_credentials";

            SpotifyTokenResponse res = restClient.post()
                    .uri(props.getTokenUrl())
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + basic)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body)
                    .retrieve()
                    .body(SpotifyTokenResponse.class);

            if (res == null || res.accessToken() == null || res.accessToken().isBlank()) {
                throw new RuntimeException("Failed to obtain Spotify app access token");
            }

            appAccessToken = res.accessToken();
            appTokenExpiresAt = Instant.now().plusSeconds(res.expiresIn());
            return appAccessToken;

        } finally {
            appTokenLock.unlock();
        }
    }



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

    public boolean hasSpotifyTokens(User user) {
        return user.getSpotifyRefreshToken() != null && !user.getSpotifyRefreshToken().isBlank();
    }

    public String forceRefreshAccessToken(User user) {
        return refreshAccessToken(user);
    }


}

