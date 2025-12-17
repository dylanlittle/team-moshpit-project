package com.makers.moshpit.controller;

import com.makers.moshpit.model.Artist;
import com.makers.moshpit.model.Post;
import com.makers.moshpit.repository.ArtistRepository;
import com.makers.moshpit.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class PostsController {
    @Autowired
    PostRepository postRepository;

    @Autowired
    ArtistRepository artistRepository;

    @PostMapping("/artists/{artistId}/posts")
    public RedirectView create(@PathVariable Long artistId, @ModelAttribute Post post) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        post.setArtist(artist);
        postRepository.save(post);

        return new RedirectView("/artists/" + artistId);
    }

}
