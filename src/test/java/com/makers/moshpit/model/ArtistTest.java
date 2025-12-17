package com.makers.moshpit.model;

import org.junit.jupiter.api.Test;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArtistTest {

    @Test
    public void artistConstructedCorrectly() {
        Artist artist = new Artist("Kendrick Lamar", "Kendrick's bio... ", "Hip Hop");
        assertThat(artist.getName(), containsString("Kendrick Lamar"));
        assertThat(artist.getBio(), containsString("Kendrick's bio... "));
        assertThat(artist.getGenre(), containsString("Hip Hop"));
    }
}
