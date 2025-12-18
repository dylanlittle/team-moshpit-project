package com.makers.moshpit.controller;

import com.makers.moshpit.model.Artist;
import com.makers.moshpit.model.Concert;
import com.makers.moshpit.model.Post;
import com.makers.moshpit.repository.ArtistRepository;
import com.makers.moshpit.repository.ConcertRepository;
import com.makers.moshpit.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;

@Controller
public class ArtistController {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @GetMapping("/artists/{id}")
    public String getArtist(@PathVariable Long id, Model model) {
        // get artist
        Artist artist =  artistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        // get all posts for artist
        Iterable<Post> posts = postRepository.findAllByArtistIdOrderByTimestampDesc(id);

        // get all concerts for artist that are in the future
        LocalDate dateToday = LocalDate.now();
        Iterable<Concert> concerts = concertRepository.findAllByArtistIdAndConcertDateAfterOrderByConcertDateAsc(id, dateToday);

        model.addAttribute("posts", posts);
        model.addAttribute("artist", artist);
        model.addAttribute("concerts", concerts);

        // Only add new Post if not already in model from RedirectView
        if (!model.containsAttribute("post")) {
            model.addAttribute("post", new Post());
        }
        return "artist_page";
    }


}
