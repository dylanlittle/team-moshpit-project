package com.makers.moshpit.controller;

import com.makers.moshpit.model.User;
import com.makers.moshpit.repository.ArtistAdminRepository;
import com.makers.moshpit.repository.UserRepository;
import com.makers.moshpit.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ArtistAdminController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ArtistAdminRepository artistAdminRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/artists/{id}/switch-to-admin")
    public String adminMode(@PathVariable Long id, Model model) {
        User user = authService.getCurrentUser();

        user.setActive_artist_id(id);
        userRepository.save(user);

        return "redirect:/artists/" + id;
    }

    @PostMapping("/artists/{id}/switch-to-user")
    public String userMode(@PathVariable Long id, Model model) {
        User user = authService.getCurrentUser();

        user.setActive_artist_id(null);
        userRepository.save(user);

        return "artist_page";
    }
}
