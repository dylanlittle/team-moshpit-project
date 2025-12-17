package com.makers.moshpit.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.ZoneId;

@ControllerAdvice
public class TimezoneInterceptor {
    @ModelAttribute("userTimezone")
    public ZoneId getUserTimezone(@CookieValue(value = "userTz", defaultValue = "UTC") String timezone) {
        try {
            return ZoneId.of(timezone);
        } catch (Exception e) {
            return ZoneId.of("UTC");
        }
    }
}