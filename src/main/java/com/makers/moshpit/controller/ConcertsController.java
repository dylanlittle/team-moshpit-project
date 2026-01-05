package com.makers.moshpit.controller;

import com.makers.moshpit.model.*;
import com.makers.moshpit.repository.ConcertGoerRepository;
import com.makers.moshpit.repository.ConcertRepository;
import com.makers.moshpit.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Controller
public class ConcertsController {

    @Autowired
    ConcertGoerRepository concertGoerRepository;

    @Autowired
    ConcertRepository concertRepository;

    @Autowired
    PostRepository postRepository;

    @GetMapping("/concerts/{concertId}")
    public String getConcert(@PathVariable Long concertId, Model model) {
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new EntityNotFoundException("Concert not found"));
        Venue venue = concert.getVenue();
        List<Post> posts = postRepository.findAllByConcertIdOrderByTimestampDesc(concertId);
        List<User> crowd = concertGoerRepository.findUsersByConcertId(concertId);
        model.addAttribute("crowd", crowd);
        model.addAttribute("posts", posts);
        model.addAttribute("concert", concert);
        model.addAttribute("venue", venue);
        return "concerts/concert_page";
    }
}
