package com.makers.moshpit.controller;

import com.makers.moshpit.model.Post;
import com.makers.moshpit.model.User;
import com.makers.moshpit.repository.PostRepository;
import com.makers.moshpit.repository.UserRepository;
import com.makers.moshpit.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UsersController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AuthService authService;

    @GetMapping("/users/after-login")
    public String afterLogin() {
        String email = authService.getAuthenticatedUserEmail();

        User user = userRepository.findUserByEmail(email)
                .orElseGet(() -> userRepository.save(new User(email)));

        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return "redirect:/users/create";
        }
        return "redirect:/user";
    }

    @GetMapping("/users/create")
    public String getCreateProfileForm(Model model) {
        model.addAttribute("currentUser", authService.getCurrentUser());
        return "/users/create_profile";
    }

    @PostMapping("/users/create")
    public String submitCreateProfileForm(@ModelAttribute User newUser) {
        User currentUser = authService.getCurrentUser();
        currentUser.setUsername(newUser.getUsername());
        currentUser.setName(newUser.getName());
        currentUser.setBio(newUser.getBio());
        userRepository.save(currentUser);
        return "redirect:/user";
    }

    @GetMapping("/user")
    public String userAccount(Model model) {
        User currentUser = authService.getCurrentUser();
        Iterable<Post> posts = postRepository.findAllByUserIdOrderByTimestampDesc(currentUser.getId());

        model.addAttribute("user", currentUser);
        model.addAttribute("posts", posts);
        return "users/user_page";
    }

    @GetMapping("/users/{id}")
    public String getUser(@PathVariable Long id, Model model) {

        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        Iterable<Post> posts = postRepository.findAllByUserIdOrderByTimestampDesc(id);

        model.addAttribute("user", user);
        model.addAttribute("posts", posts);

        return "users/user_page";
    }
}
