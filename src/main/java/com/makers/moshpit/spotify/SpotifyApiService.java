package com.makers.moshpit.spotify;

import com.makers.moshpit.model.User;
import com.makers.moshpit.spotify.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class SpotifyApiService {

    @Autowired
    private SpotifyProperties props;

    @Autowired
    private SpotifyTokenService tokenService;

    private final RestClient client;

    public SpotifyApiService(SpotifyProperties props, SpotifyTokenService tokenService) {
        this.props = props;
        this.tokenService = tokenService;
        this.client = RestClient.builder().baseUrl(props.getApiBaseUrl()).build();
    }

    public List<SpotifyArtist> getTopArtists(User user, String timeRange, int limit) {
        String token = tokenService.getValidAccessToken(user);

        SpotifyTopArtistsResponse res = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/me/top/artists")
                        .queryParam("time_range", timeRange)
                        .queryParam("limit", limit)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(SpotifyTopArtistsResponse.class);

        return res == null ? List.of() : res.items();
    }

    public List<SpotifyTrack> getTopTracks(User user, String timeRange, int limit) {
        String token = tokenService.getValidAccessToken(user);

        SpotifyTopTracksResponse res = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/me/top/tracks")
                        .queryParam("time_range", timeRange)
                        .queryParam("limit", limit)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(SpotifyTopTracksResponse.class);

        return res == null ? List.of() : res.items();
    }

    public SpotifySavedTracksResponse getSavedTracksPage(User user, int limit, int offset) {
        String token = tokenService.getValidAccessToken(user);

        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/me/tracks")
                        .queryParam("limit", limit)
                        .queryParam("offset", offset)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(SpotifySavedTracksResponse.class);
    }

    public List<SpotifySearchArtistsResponse.Item> searchArtistsApp(String query, int limit) {
        String token = tokenService.getAppAccessToken();

        SpotifySearchArtistsResponse res = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("q", query)
                        .queryParam("type", "artist")
                        .queryParam("limit", limit)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(SpotifySearchArtistsResponse.class);

        if (res == null || res.artists() == null || res.artists().items() == null) return List.of();
        return res.artists().items();
    }

}
