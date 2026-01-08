package com.makers.moshpit.controller;

import com.makers.moshpit.model.User;
import com.makers.moshpit.service.AuthService;
import com.makers.moshpit.spotify.SpotifyTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class SpotifyNowPlayingController {

    @Autowired
    private AuthService authService;

    @Autowired
    private SpotifyTokenService tokenService;

    private final RestClient restClient = RestClient.create();

    public SpotifyNowPlayingController(AuthService authService,
                                       SpotifyTokenService tokenService) {
        this.authService = authService;
        this.tokenService = tokenService;
    }

    @GetMapping("/api/spotify/now-playing")
    public ResponseEntity<?> nowPlaying() {
        User user = authService.getCurrentUser();

        if (!tokenService.hasSpotifyTokens(user)) {
            return ResponseEntity.ok(Map.of(
                    "connected", false,
                    "isPlaying", false,
                    "message", "Spotify not connected"
            ));
        }

        try {
            String accessToken = tokenService.getValidAccessToken(user);

            ResponseEntity<Map> resp = restClient.get()
                    .uri("https://api.spotify.com/v1/me/player/currently-playing")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .toEntity(Map.class);

            if (resp.getStatusCode() == HttpStatus.NO_CONTENT || resp.getBody() == null) {
                return ResponseEntity.ok(Map.of(
                        "connected", true,
                        "isPlaying", false,
                        "message", "Nothing currently playing"
                ));
            }

            Map body = resp.getBody();
            boolean isPlaying = Boolean.TRUE.equals(body.get("is_playing"));
            Integer progressMs = (Integer) body.get("progress_ms");

            Map item = (Map) body.get("item");
            if (item == null) {
                return ResponseEntity.ok(Map.of(
                        "connected", true,
                        "isPlaying", false,
                        "message", "Nothing currently playing"
                ));
            }

            String trackName = (String) item.get("name");
            Integer durationMs = (Integer) item.get("duration_ms");

            List<Map> artistsList = (List<Map>) item.get("artists");
            String artists = artistsList == null ? "" :
                    (String) artistsList.stream()
                            .map(a -> (String) a.get("name"))
                            .collect(Collectors.joining(", "));

            Map album = (Map) item.get("album");
            String albumName = album == null ? null : (String) album.get("name");

            String albumImageUrl = null;
            if (album != null) {
                List<Map> images = (List<Map>) album.get("images");
                if (images != null && !images.isEmpty()) {
                    albumImageUrl = (String) images.get(0).get("url");
                }
            }

            String spotifyUrl = null;
            Map externalUrls = (Map) item.get("external_urls");
            if (externalUrls != null) {
                spotifyUrl = (String) externalUrls.get("spotify");
            }

            return ResponseEntity.ok(Map.of(
                    "connected", true,
                    "isPlaying", isPlaying,
                    "trackName", trackName,
                    "artists", artists,
                    "albumName", albumName,
                    "albumImageUrl", albumImageUrl,
                    "spotifyUrl", spotifyUrl,
                    "progressMs", progressMs,
                    "durationMs", durationMs
            ));

        } catch (RestClientResponseException e) {
            // If token refresh fails or Spotify errors
            return ResponseEntity.ok(Map.of(
                    "connected", true,
                    "isPlaying", false,
                    "message", "Could not load now playing (" + e.getRawStatusCode() + ")"
            ));
        } catch (RuntimeException e) {
            // Your refreshAccessToken throws "Spotify not connected"
            return ResponseEntity.ok(Map.of(
                    "connected", false,
                    "isPlaying", false,
                    "message", e.getMessage()
            ));
        }
    }
}

