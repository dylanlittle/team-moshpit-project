package com.makers.moshpit.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class VenueTest {

    @Test
    public void VenueConstructsCorrectly() {

        Venue venue = new Venue("Name", "City", "Country", "Address");

        assertThat(venue.getVenueName(), equalTo("Name"));
        assertThat(venue.getCity(), equalTo("City"));
        assertThat(venue.getCountry(), equalTo("Country"));
        assertThat(venue.getAddress(), equalTo("Address"));

    }
}
