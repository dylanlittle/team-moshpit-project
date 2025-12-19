package com.makers.moshpit.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

public class UserTest {

    @Test
    public void UserConstructsCorrectly() {

        User user = new User("test@testmail.org");

        assertThat(user.getEmail(), equalTo("test@testmail.org"));
        assertThat(user.getName(), equalTo(""));
        assertThat(user.getUsername(), equalTo(""));
        assertThat(user.getBio(), equalTo(""));
        assertThat(user.getLocation(), equalTo(""));
        assertNull(user.getAvatar(), "The avatar should be null");
    }
}