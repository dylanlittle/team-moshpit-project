package com.makers.moshpit.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ConcertForm {
    private LocalDate concertDate;
    public LocalTime StartTime;
    private String venueName;
    private String city;
    private String country;
    private String address;
}
