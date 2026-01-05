package com.makers.moshpit.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConcertTest {

    @Test
    public void ConcertConstructsCorrectly() {

        Artist artist = mock(Artist.class);
        Venue venue = mock(Venue.class);
        LocalDate concertDate = LocalDate.parse("2025-12-25");
        LocalTime startTime = LocalTime.now();

        Concert concert = new Concert(concertDate, startTime, venue, artist);

        assertThat(concert.getConcertDate(), equalTo(concertDate));
        assertThat(concert.getStartTime(), equalTo(startTime));
        assertThat(concert.getVenue(), equalTo(venue));
        assertThat(concert.getArtist(), equalTo(artist));

    }
}
