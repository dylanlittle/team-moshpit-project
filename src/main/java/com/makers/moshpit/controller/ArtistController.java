package com.makers.moshpit.controller;

import com.makers.moshpit.model.*;
import com.makers.moshpit.repository.*;
import com.makers.moshpit.service.AuthService;
import com.makers.moshpit.spotify.relationship.RelationshipService;
import com.makers.moshpit.spotify.relationship.RelationshipStats;
import com.makers.moshpit.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ArtistController {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ArtistAdminRepository artistAdminRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private RelationshipService relationshipService;

    @Autowired
    private MediaService mediaService;


    @GetMapping("/artists/{id}")
    public String getArtist(@PathVariable Long id, Model model,
                            @RequestParam(value="timeRange", required=false) String timeRange) {
        User currentUser = authService.getCurrentUser();

        List<ArtistAdmin> admins = artistAdminRepository.findAdminsByArtistId(id);

        Artist artist =  artistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        boolean isFollowing = followRepository.findByUserAndArtist(currentUser, artist).isPresent();

        Iterable<Post> posts = postRepository.findAllByArtistIdOrderByTimestampDesc(id);

        LocalDate dateToday = LocalDate.now();
        Iterable<Concert> futureConcerts = concertRepository.findAllByArtistIdAndConcertDateAfterOrderByConcertDateAsc(id, dateToday);
        Iterable<Concert> pastConcerts = concertRepository.findAllByArtistIdAndConcertDateBeforeOrderByConcertDateDesc(id, dateToday);

        model.addAttribute("timeRange", timeRange == null ? "medium_term" : timeRange);

        if (currentUser != null) {
            RelationshipStats stats = relationshipService.build(currentUser, artist, timeRange);
            model.addAttribute("relationshipStats", stats);
        }

        model.addAttribute("posts", posts);
        model.addAttribute("artist", artist);
        model.addAttribute("futureConcerts", futureConcerts);
        model.addAttribute("pastConcerts", pastConcerts);
        model.addAttribute("admins", admins);
        model.addAttribute("isFollowing", isFollowing);

        if (!model.containsAttribute("post")) {
            model.addAttribute("post", new Post());
        }
        return "artists/artist_page";
    }
    @GetMapping("/artists/new")
    public String showArtistCreateForm() {
        return "artists/artist_create";
    }

    @PostMapping("/artists")
    public RedirectView createArtist(
            @ModelAttribute Artist artist,
            @AuthenticationPrincipal OAuth2User principal,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {

        String email = principal.getAttribute("email");
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        artist.setVerified(false);
        artist.setCreatedBy(user.getId());

        if (image != null && !image.isEmpty()) {

            if (image.getSize() > (10 * 1024 * 1024)) {
                throw new RuntimeException("File too large — max 10MB");
            }

            try {
                String imageUrl = mediaService.uploadImage(image);
                artist.setAvatar(imageUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Artist savedArtist = artistRepository.save(artist);

        ArtistAdmin adminLink = new ArtistAdmin();
        adminLink.setArtist(savedArtist);
        adminLink.setUser(user);
        adminLink.setRole(ArtistAdmin.Role.OWNER);
        artistAdminRepository.save(adminLink);

        return new RedirectView("/artists/" + savedArtist.getId());
    }

    @GetMapping("/artists/{id}/update")
    public String getArtistUpdateForm(@PathVariable Long id, Model model) {

        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        model.addAttribute("artist", artist);

        return "artists/artist_edit";
    }

    @PostMapping("/artists/{id}/update")
    public String updateArtist(
            @PathVariable Long id,
            @ModelAttribute Artist updatedArtist,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        // 1. Fetch the existing artist from the database
        Artist existingArtist = artistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        // 2. Update only the fields allowed in the form
        existingArtist.setName(updatedArtist.getName());
        existingArtist.setGenre(updatedArtist.getGenre());
        existingArtist.setBio(updatedArtist.getBio());

        // 3. Handle the image only if a new file was actually selected
        if (image != null && !image.isEmpty()) {
            try {
                // Check file size (e.g., 10MB limit)
                if (image.getSize() > (10 * 1024 * 1024)) {
                    throw new RuntimeException("File too large");
                }

                // Upload new image and update the avatar URL
                String imageUrl = mediaService.uploadImage(image);
                existingArtist.setAvatar(imageUrl);
            } catch (Exception e) {
                e.printStackTrace();
                // Optionally add an error message to redirectAttributes here
            }
        }

        // 4. Save the merged artist back to the database
        artistRepository.save(existingArtist);

        // 5. Redirect back to the artist profile page
        return "redirect:/artists/" + id;
    }

}




