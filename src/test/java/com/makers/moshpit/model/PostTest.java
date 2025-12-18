package com.makers.moshpit.model;

import org.junit.jupiter.api.Test;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class PostTest {

    @Test
    public void postConstructedCorrectly() {

        User user = new User("email@email.com");
        user.setId(1L);
        Post post = new Post("This is the post content", user);

        assertThat(post.getContent(), containsString("This is the post content"));
    }
}
