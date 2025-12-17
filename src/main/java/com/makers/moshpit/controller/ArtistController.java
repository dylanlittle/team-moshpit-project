package com.makers.moshpit.controller;

import com.makers.moshpit.model.Artist;
import com.makers.moshpit.model.Post;
import com.makers.moshpit.repository.ArtistRepository;
import com.makers.moshpit.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.ZoneId;

@Controller
public class ArtistController {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/artists/{id}")
    public String getArtist(@PathVariable Long id, @CookieValue(value = "userTz", required = false) String userTz, Model model) {
        Artist artist =  artistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artist not found"));
        Iterable<Post> posts = postRepository.findAllByArtistIdOrderByTimestampDesc(id);
        ZoneId zoneId = (userTz != null) ? ZoneId.of(userTz) : ZoneId.systemDefault();
        model.addAttribute("userTimeZone", zoneId.getId());
        model.addAttribute("posts", posts);
        model.addAttribute("artist", artist);
        model.addAttribute("post", new Post());
        return "artist_page";
    }


}
