package com.makers.moshpit.controller;

import com.makers.moshpit.model.User;
import com.makers.moshpit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user")
    public String userAccount(OidcUser principal, Model model) {

        String email = principal.getEmail();

        User user = userRepository
                .findUserByEmail(email)
                .orElseGet(() -> userRepository.save(new User(email)));

        model.addAttribute("user", user);

        return "user_page";
    }
}
