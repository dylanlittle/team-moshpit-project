package com.makers.moshpit.controller;

import com.makers.moshpit.model.*;
import com.makers.moshpit.repository.*;
import com.makers.moshpit.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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


    @GetMapping("/artists/{id}")
    public String getArtist(@PathVariable Long id, Model model) {
        User currentUser = authService.getCurrentUser();

        List<ArtistAdmin> admins = artistAdminRepository.findAdminsByArtistId(id);

        Artist artist =  artistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        boolean isFollowing = followRepository.findByUserAndArtist(currentUser, artist).isPresent();

        Iterable<Post> posts = postRepository.findAllByArtistIdOrderByTimestampDesc(id);

        LocalDate dateToday = LocalDate.now();
        Iterable<Concert> concerts = concertRepository.findAllByArtistIdAndConcertDateAfterOrderByConcertDateAsc(id, dateToday);

        model.addAttribute("posts", posts);
        model.addAttribute("artist", artist);
        model.addAttribute("concerts", concerts);
        model.addAttribute("admins", admins);
        model.addAttribute("isFollowing", isFollowing);

        if (!model.containsAttribute("post")) {
            model.addAttribute("post", new Post());
        }
        return "artist_page";
    }
    @GetMapping("/artists/new")
    public String showArtistCreateForm() {
        return "artist_create";
    }

    @PostMapping("/artists")
    public RedirectView createArtist(
            @ModelAttribute Artist artist,
            @AuthenticationPrincipal OAuth2User principal
    ) {

        String email = principal.getAttribute("email");
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        artist.setVerified(false);
        artist.setCreatedBy(user.getId());

        Artist savedArtist = artistRepository.save(artist);

        ArtistAdmin adminLink = new ArtistAdmin();
        adminLink.setArtist(savedArtist);
        adminLink.setUser(user);
        adminLink.setRole(ArtistAdmin.Role.OWNER);
        artistAdminRepository.save(adminLink);

        return new RedirectView("/artists/" + savedArtist.getId());
    }

}




