package com.makers.moshpit.controller;

import com.makers.moshpit.model.Artist;
import com.makers.moshpit.model.Concert;
import com.makers.moshpit.model.Post;
import com.makers.moshpit.model.User;
import com.makers.moshpit.repository.ArtistRepository;
import com.makers.moshpit.repository.ConcertGoerRepository;
import com.makers.moshpit.repository.PostRepository;
import com.makers.moshpit.repository.UserRepository;
import com.makers.moshpit.service.AuthService;
import com.makers.moshpit.service.MediaService;
import com.makers.moshpit.spotify.CurrentUserService;
import com.makers.moshpit.spotify.SpotifyApiService;
import com.makers.moshpit.spotify.dto.SpotifyArtist;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
import java.security.Principal;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    SpotifyApiService spotifyApiService;

    @Autowired
    private ConcertGoerRepository concertGoerRepository;

    public void SpotifyTopArtistsController(SpotifyApiService spotifyApi,
                                            CurrentUserService currentUserService,
                                            ArtistRepository artistRepository) {
        this.spotifyApiService = spotifyApi;
        this.currentUserService = currentUserService;
        this.artistRepository = artistRepository;
    }

    @GetMapping("/users/after-login")
    public String afterLogin() {
        String email = authService.getAuthenticatedUserEmail();

        User user = userRepository.findUserByEmail(email)
                .orElseGet(() -> userRepository.save(new User(email)));

        if (user.getUsername() == null
                || user.getUsername().equals("null")
                || user.getUsername().isEmpty()
                || user.getName() == null
                || user.getName().equals("null")
                || user.getName().isEmpty()) {

            return "redirect:/users/create";
        }
        return "redirect:/";
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
        form.setAvatar(currentUser.getAvatar());
        model.addAttribute("newUser", form);
        model.addAttribute("currentUser", currentUser);
        return "/users/create_profile";
    }

    @PostMapping("/users/create")
    public String submitCreateProfileForm(@Valid @ModelAttribute("newUser") CompleteProfileRequest newUser,
                                          BindingResult result,
                                          RedirectAttributes redirectAttributes,
                                          @RequestParam(value = "image", required = false)MultipartFile imageFile) {

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
    public String userAccount(Model model,
                              @AuthenticationPrincipal OAuth2User principal,
                              @RequestParam(defaultValue = "short_term") String timeRange) {
        User currentUser = currentUserService.getOrCreateFromPrincipal(principal);
        Iterable<Post> posts = postRepository.findAllByUserIdOrderByTimestampDesc(currentUser.getId());
        List<Artist> myArtists =  artistRepository.findArtistsByUserId(currentUser.getId());
        model.addAttribute("user", currentUser);
        model.addAttribute("posts", posts);
        model.addAttribute("myArtists", myArtists);
        model.addAttribute("isOwner", true);

        if (!timeRange.equals("short_term") && !timeRange.equals("medium_term") && !timeRange.equals("long_term")) {
            timeRange = "short_term";
        }

        if (currentUser.getSpotifyRefreshToken() != null && !currentUser.getSpotifyRefreshToken().isBlank()) {
            List<SpotifyArtist> topArtists = spotifyApiService.getTopArtists(currentUser, timeRange, 10);
            model.addAttribute("spotifyTopArtists", topArtists);

            Map<String, Artist> suggestions = new LinkedHashMap<>();
            for (SpotifyArtist a : topArtists) {
                artistRepository.findFirstByNameIgnoreCase(a.name())
                        .ifPresent(match -> suggestions.put(a.id(), match));
            }
            model.addAttribute("spotifySuggestions", suggestions);
        }

        Iterable<Artist> artists = artistRepository.findAllArtistsFollowedByUser(currentUser);
        Iterable<Concert> concerts = concertGoerRepository.findConcertsByUserId(currentUser.getId());

        model.addAttribute("timeRange", timeRange);
        model.addAttribute("user", currentUser);
        model.addAttribute("posts", posts);
        model.addAttribute("followedArtists", artists);
        model.addAttribute("editing", false);
        model.addAttribute("concerts", concerts);
        return "users/user_page";
    }

    @PostMapping("/user/spotify/privacy")
    public String updateSpotifyPrivacy(@AuthenticationPrincipal OAuth2User principal,
                                       @RequestParam(required = false) String shareTopArtists) {

        User currentUser = currentUserService.getOrCreateFromPrincipal(principal);
        currentUser.setShareSpotifyTopArtists(shareTopArtists != null);
        userRepository.save(currentUser);

        return "redirect:/user";
    }


    @GetMapping("/users/{id}")
    public String getUser(@PathVariable Long id,
                          Model model,
                          @RequestParam(defaultValue = "short_term") String timeRange) {

        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        User currentUser = authService.getCurrentUser();

        if (currentUser != null && currentUser.getId().equals(id)) {
            return "redirect:/user";
        }

        boolean profileSpotifyConnected = user.getSpotifyRefreshToken() != null
                && !user.getSpotifyRefreshToken().isBlank();

        boolean canShowPublicTopArtists = profileSpotifyConnected && user.isShareSpotifyTopArtists();
        model.addAttribute("canShowPublicTopArtists", canShowPublicTopArtists);

        if (canShowPublicTopArtists) {
            if (!timeRange.equals("short_term") && !timeRange.equals("medium_term") && !timeRange.equals("long_term")) {
                timeRange = "short_term";
            }
            List<SpotifyArtist> topArtists = spotifyApiService.getTopArtists(user, timeRange, 10);
            model.addAttribute("spotifyTopArtists", topArtists);
            model.addAttribute("timeRange", timeRange);
        }

        Iterable<Post> posts = postRepository.findAllByUserIdOrderByTimestampDesc(id);

        Iterable<Artist> artists = artistRepository.findAllArtistsFollowedByUser(user);

        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        model.addAttribute("followedArtists", artists);
        model.addAttribute("editing", false);
        model.addAttribute("isOwner", false);

        return "users/user_page";
    }
    @PostMapping("/users/{id}")
    public String updateProfile(
            @PathVariable Long id,
            /*@RequestParam String username,*/
            @RequestParam(required = false) String bio, MultipartFile image, String username, String location) {


        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User currentUser = authService.getCurrentUser();

        if (currentUser == null || !currentUser.getId().equals(user.getId())) {
            return "redirect:/users/" + id;
        }

        if (username != null && !username.isEmpty()) {
            user.setUsername(username);
        }
        if (bio != null && !bio.isEmpty()) {
            user.setBio(bio);
        }
        if (location != null && !location.isEmpty()) {
            user.setLocation(location);
        }

        if (image != null && !image.isEmpty()) {

            if (image.getSize() > (10 * 1024 * 1024)) {
                throw new RuntimeException("File too large — max 10MB");
            }

            try {
                String imageUrl = mediaService.uploadImage(image);
                currentUser.setAvatar(imageUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        userRepository.save(user);

        return "redirect:/users/" + id;
    }


    @GetMapping("/users/{id}/edit")
    public String editProfile(@PathVariable Long id, Model model, Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User currentUser = authService.getCurrentUser();

        if (currentUser == null || !currentUser.getId().equals(user.getId())) {
            return "redirect:/users/" + id;
        }

        model.addAttribute("user", user);
        model.addAttribute("editing", true);
        model.addAttribute("isOwner", true);
        return "users/user_page";
    }

    @GetMapping("/users/edit")
    public String editProfile(Model model) {
        User user = authService.getCurrentUser();
        model.addAttribute("user", user);
        return "users/edit_profile";
    }

}
