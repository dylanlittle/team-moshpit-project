package com.makers.moshpit.controller;

import com.makers.moshpit.model.Post;
import com.makers.moshpit.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PostsController {
    @Autowired
    PostRepository postRepository;

    @PostMapping("/posts")
    public String create(Post post) {
        postRepository.save(post);
        return "post added";
    }
}
