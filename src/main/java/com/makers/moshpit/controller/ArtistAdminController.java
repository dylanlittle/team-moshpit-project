package com.makers.moshpit.controller;

import com.makers.moshpit.model.User;
import com.makers.moshpit.repository.ArtistAdminRepository;
import com.makers.moshpit.repository.UserRepository;
import com.makers.moshpit.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
    public String adminMode(@PathVariable Long id) {
        User user = authService.getCurrentUser();

        user.setActive_artist_id(id);
        userRepository.save(user);

        return "redirect:/artists/" + id;
    }

    @PostMapping("/switch-to-user")
    public String userMode(HttpServletRequest request) {
        User user = authService.getCurrentUser();

        user.setActive_artist_id(null);
        userRepository.save(user);

        String referer = request.getHeader("Referer");

        if (referer == null || referer.isEmpty()) {
            return "redirect:/";
        }

        return "redirect:" + referer;
    }
}
