package com.makers.moshpit.controller;

import com.makers.moshpit.model.Artist;
import com.makers.moshpit.model.Post;
import com.makers.moshpit.repository.ArtistRepository;
import com.makers.moshpit.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {


    @Autowired
    private PostRepository postRepository;

    @GetMapping("/users/{id}")
    public String getUser(@PathVariable Long id, Model model) {

        // 1. Mock the User (Since you don't have a User class, a Map works perfectly for Thymeleaf)
        Map<String, Object> dummyUser = new HashMap<>();
        dummyUser.put("id", id);
        dummyUser.put("name", "Ollie Jeffcote");
        dummyUser.put("bio", "Avid music fan and software engineer.");

        // 2. Mock the Posts (Creating a few hardcoded Post objects)
        List<Post> dummyPosts = new ArrayList<>();
        Post p1 = new Post();
        p1.setContent("Just saw Spacey Jane live! Incredible.");

        Post p2 = new Post();
        p2.setContent("New Haim album is on repeat.");

        dummyPosts.add(p1);
        dummyPosts.add(p2);

        // 3. Add to model
        model.addAttribute("user", dummyUser);
        model.addAttribute("posts", dummyPosts);
        model.addAttribute("post", new Post());

        return "user_page";
    }
}
