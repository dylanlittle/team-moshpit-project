package com.makers.moshpit.controller;

import com.makers.moshpit.model.Concert;
import com.makers.moshpit.model.Post;
import com.makers.moshpit.model.User;
import com.makers.moshpit.repository.ConcertRepository;
import com.makers.moshpit.repository.PostRepository;
import com.makers.moshpit.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class HomeController {

    @Autowired
    AuthService authService;

    @Autowired
    ConcertRepository concertRepository;

    @Autowired
    PostRepository postRepository;

    @GetMapping({"/", "/home"})
    public String home(Model model) {

        User user = authService.getCurrentUser();
        Iterable<Post> posts = postRepository.findAll();

        LocalDate dateToday = LocalDate.now();
        Iterable<Concert> concerts = concertRepository.findByConcertDateAfterOrderByConcertDateAsc(dateToday);

        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        model.addAttribute("concerts", concerts);

        return "index";
    }


}
