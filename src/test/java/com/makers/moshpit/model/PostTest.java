package com.makers.moshpit.model;

import org.junit.jupiter.api.Test;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class PostTest {

    @Test
    public void postConstructedCorrectly() {
        Post post = new Post("This is the post content");

        assertThat(post.getContent(), containsString("This is the post content"));
    }
}
