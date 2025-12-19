package com.makers.moshpit.spotify;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "spotify")
public class SpotifyProperties {
    private String clientId;
    private String clientSecret;
    private String authorizeUrl;
    private String tokenUrl;
    private String apiBaseUrl;
    private String redirectUri;


}
