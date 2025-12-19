package com.makers.moshpit.model;

import org.junit.jupiter.api.Test;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;

public class PostTest {

    @Test
    public void postConstructedCorrectly() {

        User user = mock(User.class);
        Post post = new Post("This is the post content", user);

        assertThat(post.getContent(), containsString("This is the post content"));
        assertThat(post.getUser(), equalTo(user));
    }
}
