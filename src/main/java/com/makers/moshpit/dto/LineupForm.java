package com.makers.moshpit.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class LineupForm {
    private List<Long> artistIds = new ArrayList<>();
}
