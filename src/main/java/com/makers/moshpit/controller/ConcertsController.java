package com.makers.moshpit.controller;

import com.makers.moshpit.model.ConcertGoer;
import com.makers.moshpit.model.User;
import com.makers.moshpit.repository.ConcertGoerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ConcertsController {

    @Autowired
    ConcertGoerRepository concertGoerRepository;

    @GetMapping("/concerts/{concertId}")
    public String getConcert(@PathVariable Long concertId, Model model) {
        List<User> crowd = concertGoerRepository.findUserByConcertId(concertId);
        model.addAttribute("crowd", crowd);
        return "concerts/concert_page";
    }
}
