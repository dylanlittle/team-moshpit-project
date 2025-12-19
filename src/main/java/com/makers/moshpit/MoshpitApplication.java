package com.makers.moshpit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(com.makers.moshpit.spotify.SpotifyProperties.class)
@SpringBootApplication
public class MoshpitApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoshpitApplication.class, args);
	}

}
