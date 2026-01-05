package com.makers.moshpit.controller;

import com.makers.moshpit.model.Artist;
import com.makers.moshpit.model.Post;
import com.makers.moshpit.model.User;
import com.makers.moshpit.repository.ArtistRepository;
import com.makers.moshpit.repository.PostRepository;
import com.makers.moshpit.repository.UserRepository;
import com.makers.moshpit.service.AuthService;
import com.makers.moshpit.service.MediaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UsersController {
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private MediaService mediaService;

    @GetMapping("/users/after-login")
    public String afterLogin() {
        String email = authService.getAuthenticatedUserEmail();

        User user = userRepository.findUserByEmail(email)
                .orElseGet(() -> userRepository.save(new User(email)));

        if (user.getUsername() == null || user.getUsername().equals("null") || user.getUsername().isEmpty()) {
            return "redirect:/users/create";
        }
        return "redirect:/user";
    }

    @Data
    public static class CompleteProfileRequest {
        @NotBlank(message = "Please provide a valid username")
        private String username;
        @NotBlank(message = "Please provide a valid name")
        private String name;
        private String bio;
        private String avatar;
        @NotBlank(message = "Please provide a valid location i.e. London, UK")
        private String location;
    }

    @GetMapping("/users/create")
    public String getCreateProfileForm(Model model) {
        User currentUser = authService.getCurrentUser();
        CompleteProfileRequest form = new CompleteProfileRequest();
        form.setUsername(currentUser.getUsername());
        form.setName(currentUser.getName());
        form.setBio(currentUser.getBio());
        form.setLocation(currentUser.getLocation());
        form.setAvatar(currentUser.getLocation());
        model.addAttribute("newUser", form);
        model.addAttribute("currentUser", currentUser);
        return "/users/create_profile";
    }

    @PostMapping("/users/create")
    public String submitCreateProfileForm(@Valid @ModelAttribute("newUser") CompleteProfileRequest newUser,
                                          BindingResult result,
                                          RedirectAttributes redirectAttributes,
                                          @RequestParam("image")MultipartFile imageFile) {

        if (result.hasErrors()) {
            return "/users/create_profile";
        }
        User currentUser = authService.getCurrentUser();
        currentUser.setUsername(newUser.getUsername());
        currentUser.setName(newUser.getName());
        currentUser.setBio(newUser.getBio());
        currentUser.setLocation(newUser.getLocation());
        if (!imageFile.isEmpty()) {
            if (imageFile.getSize() > (10 * 1024 * 1024)) {
                throw new RuntimeException("File too large — maximum allowed size is 10MB.");
            }

            try {
                String imageUrl = mediaService.uploadImage(imageFile);
                currentUser.setAvatar(imageUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        userRepository.save(currentUser);
        return "redirect:/user";
    }

    @GetMapping("/user")
    public String userAccount(Model model) {
        User currentUser = authService.getCurrentUser();
        Iterable<Post> posts = postRepository.findAllByUserIdOrderByTimestampDesc(currentUser.getId());
        List<Artist> myArtists =  artistRepository.findArtistsByUserId(currentUser.getId());
        model.addAttribute("user", currentUser);
        model.addAttribute("posts", posts);
        model.addAttribute("myArtists", myArtists);
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
