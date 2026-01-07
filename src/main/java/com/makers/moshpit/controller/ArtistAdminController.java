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
import com.makers.moshpit.model.Artist;
import com.makers.moshpit.model.ArtistAdmin;
import com.makers.moshpit.repository.ArtistRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class ArtistAdminController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ArtistAdminRepository artistAdminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArtistRepository artistRepository;

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

    @GetMapping("/artists/{id}/newAdmin")
    public String showArtistCreateForm(
            @RequestParam(name = "query", required = false) String query,
            @PathVariable Long id,
            Model model)  {
        User currentUser = authService.getCurrentUser();
        boolean canEdit = currentUser != null &&
                artistAdminRepository.existsByArtistIdAndUserId(id, currentUser.getId());
        if(!canEdit) {
            return "redirect:/artists/" + id;
        }
        List<ArtistAdmin> admins = artistAdminRepository.findAdminsByArtistId(id);
        model.addAttribute("admins", admins);
        model.addAttribute("id", id);
        if (query == null || query.trim().isEmpty()) {
            model.addAttribute("query", "");
            model.addAttribute("artistResults", java.util.Collections.emptyList());
            model.addAttribute("userResults", java.util.Collections.emptyList());
            model.addAttribute("suggestedResults", java.util.Collections.emptyList());
            return "admin/add_artist_admin";
        }

        String trimmedQuery = query.trim();
        List<User> userResults = userRepository.findByUsernameContainingIgnoreCase(trimmedQuery);

        Set<Long> adminUserIds = admins.stream()
                .map(ArtistAdmin::getUser)
                .map(User::getId)
                .collect(Collectors.toSet());
        userResults.removeIf(user -> adminUserIds.contains(user.getId()));



        model.addAttribute("query", query);
        model.addAttribute("userResults", userResults);
        return "admin/add_artist_admin";
    }

    @PostMapping("/artists/{id}/newAdmin")
    public String showArtistCreateForm(
            @RequestParam(name = "query", required = false) String query,
            @RequestParam("userId") Long userId,
            @PathVariable Long id,
            Model model)  {
        User currentUser = authService.getCurrentUser();
        boolean canEdit = currentUser != null &&
                artistAdminRepository.existsByArtistIdAndUserId(id, currentUser.getId());
        if(!canEdit) {
            return "redirect:/artists/" + id;
        }
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Artist not found"));

        User newAdminUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        ArtistAdmin adminLink = new ArtistAdmin();
        adminLink.setArtist(artist);
        adminLink.setUser(newAdminUser);
        adminLink.setRole(ArtistAdmin.Role.ADMIN);
        artistAdminRepository.save(adminLink);

        List<ArtistAdmin> admins = artistAdminRepository.findAdminsByArtistId(id);

        model.addAttribute("id", id);
        model.addAttribute("admins", admins);

        if (query == null || query.trim().isEmpty()) {
            model.addAttribute("query", "");
            model.addAttribute("artistResults", java.util.Collections.emptyList());
            model.addAttribute("userResults", java.util.Collections.emptyList());
            model.addAttribute("suggestedResults", java.util.Collections.emptyList());
            return "admin/add_artist_admin";
        }

        String trimmedQuery = query.trim();
        List<User> userResults = userRepository.findByUsernameContainingIgnoreCase(trimmedQuery);
        Set<Long> adminUserIds = admins.stream()
                .map(ArtistAdmin::getUser)
                .map(User::getId)
                .collect(Collectors.toSet());
        userResults.removeIf(user -> adminUserIds.contains(user.getId()));
        model.addAttribute("query", query);
        model.addAttribute("userResults", userResults);
        return "admin/add_artist_admin";
    }
}
