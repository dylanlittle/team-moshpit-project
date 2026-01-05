package com.makers.moshpit.controller;

import com.makers.moshpit.model.*;
import com.makers.moshpit.repository.ConcertGoerRepository;
import com.makers.moshpit.repository.ConcertRepository;
import com.makers.moshpit.repository.PostRepository;
import com.makers.moshpit.service.AuthService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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

    @Autowired
    AuthService authService;

    @GetMapping("/concerts/{concertId}")
    public String getConcert(@PathVariable Long concertId, Model model) {
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new EntityNotFoundException("Concert not found"));
        Venue venue = concert.getVenue();
        List<Post> posts = postRepository.findAllByConcertIdOrderByTimestampDesc(concertId);
        List<User> crowd = concertGoerRepository.findUsersByConcertId(concertId);
        boolean isGoing = concertGoerRepository.existsByUserAndConcert(authService.getCurrentUser(), concert);
        model.addAttribute("crowd", crowd);
        model.addAttribute("posts", posts);
        model.addAttribute("concert", concert);
        model.addAttribute("venue", venue);
        model.addAttribute("isGoing", isGoing);
        return "concerts/concert_page";
    }

    @PostMapping("/concerts/{concertId}/rsvp")
    public ResponseEntity<Void> rsvp(@PathVariable Long concertId) {
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new EntityNotFoundException("Concert not found"));
        User currentUser = authService.getCurrentUser();
        boolean isGoing = concertGoerRepository.existsByUserAndConcert(currentUser, concert);
        if (isGoing) {
            concertGoerRepository.deleteByUserAndConcert(currentUser, concert);
        } else {
            concertGoerRepository.save(new ConcertGoer(currentUser, concert));
        }
        return ResponseEntity.ok().build();
    }
}
