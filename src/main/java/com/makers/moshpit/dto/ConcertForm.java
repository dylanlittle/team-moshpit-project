package com.makers.moshpit.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ConcertForm {
    private String concertName;
    private LocalDate concertDate;
    private LocalTime startTime;
    private String venueName;
    private String city;
    private String country;
    private String address;
    /*private String image;*/
}
