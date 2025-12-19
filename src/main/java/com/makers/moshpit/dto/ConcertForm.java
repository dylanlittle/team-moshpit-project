package com.makers.moshpit.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ConcertForm {
    private LocalDate concertDate;
    private String venueName;
    private String city;
    private String country;
    private String address;
}
