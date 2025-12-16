package com.makers.moshpit.controller;

import com.makers.moshpit.model.Post;
import com.makers.moshpit.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PostsController {
    @Autowired
    PostRepository postRepository;

    @PostMapping("/posts")
    public String create(Model model, @ModelAttribute Post post) {
        postRepository.save(post);
        return "post added";
    }
}
