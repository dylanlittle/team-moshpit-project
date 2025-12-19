package com.makers.moshpit.spotify;

import com.makers.moshpit.model.User;
import com.makers.moshpit.spotify.dto.SpotifyArtist;
import com.makers.moshpit.spotify.dto.SpotifyTopArtistsResponse;
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
}
