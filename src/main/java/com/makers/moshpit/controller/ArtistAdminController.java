package com.makers.moshpit.controller;

import com.makers.moshpit.model.User;
import com.makers.moshpit.repository.ArtistAdminRepository;
import com.makers.moshpit.repository.UserRepository;
import com.makers.moshpit.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.makers.moshpit.model.Artist;
import com.makers.moshpit.model.ArtistAdmin;
import com.makers.moshpit.repository.ArtistRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
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
    public String manageArtistAdmins(
            @RequestParam(name = "query", required = false) String query,
            @PathVariable Long id,
            Model model
    ) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Artist not found"));

        List<ArtistAdmin> admins = artistAdminRepository.findAdminsByArtistId(id);

        model.addAttribute("id", id);
        model.addAttribute("admins", admins);
        model.addAttribute("query", query == null ? "" : query);

        if (query != null && !query.isBlank()) {
            List<User> userResults = userRepository
                    .findByUsernameContainingIgnoreCase(query.trim());

            Set<Long> adminUserIds = admins.stream()
                    .map(a -> a.getUser().getId())
                    .collect(Collectors.toSet());

            userResults.removeIf(u -> adminUserIds.contains(u.getId()));

            model.addAttribute("userResults", userResults);
        } else {
            model.addAttribute("userResults", List.of());
        }

        return "admin/manage_artist_admin";
    }


    @PostMapping("/artists/{id}/newAdmin")
    public String addArtistAdmin(
            @RequestParam(name = "query", required = false) String query,
            @RequestParam("userId") Long userId,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        User currentUser = authService.getCurrentUser();
        boolean canEdit = currentUser != null &&
                artistAdminRepository.existsByArtistIdAndUserId(id, currentUser.getId());

        if (!canEdit) {
            return "redirect:/artists/" + id;
        }

        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Artist not found"));

        // prevent duplicates
        if (!artistAdminRepository.existsByArtistIdAndUserId(id, userId)) {
            User newAdminUser = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            ArtistAdmin adminLink = new ArtistAdmin();
            adminLink.setArtist(artist);
            adminLink.setUser(newAdminUser);
            adminLink.setRole(ArtistAdmin.Role.ADMIN);
            artistAdminRepository.save(adminLink);
        }

        if (query != null && !query.isBlank()) {
            return "redirect:/artists/" + id + "/newAdmin?query=" +
                    UriUtils.encode(query, StandardCharsets.UTF_8);
        }

        return "redirect:/artists/" + id + "/newAdmin";
    }

    @PostMapping("/artists/{artistId}/admin/{adminId}")
    @Transactional
    public String deleteAdmin(
            @PathVariable Long artistId,
            @PathVariable Long adminId,
            RedirectAttributes redirectAttributes) {

        User currentUser = authService.getCurrentUser();

        boolean canEdit = currentUser != null &&
                artistAdminRepository.existsByArtistIdAndUserId(artistId, currentUser.getId());
        if (!canEdit) {
            redirectAttributes.addFlashAttribute("error", "Access denied");
            return "redirect:/artists/" + artistId + "/newAdmin";
        }

        ArtistAdmin targetAdmin = artistAdminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));

        if (targetAdmin.getRole() == ArtistAdmin.Role.OWNER) {
            redirectAttributes.addFlashAttribute("error", "Cannot remove artist owner");
            return "redirect:/artists/" + artistId + "/newAdmin";
        }

        artistAdminRepository.deleteById(adminId);
        redirectAttributes.addFlashAttribute("success", "Admin removed successfully");
        return "redirect:/artists/" + artistId + "/newAdmin";
    }
}
