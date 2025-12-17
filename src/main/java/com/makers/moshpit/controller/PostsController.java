package com.makers.moshpit.controller;

import com.makers.moshpit.model.Artist;
import com.makers.moshpit.model.Post;
import com.makers.moshpit.repository.ArtistRepository;
import com.makers.moshpit.repository.PostRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class PostsController {
    @Autowired
    PostRepository postRepository;

    @Autowired
    ArtistRepository artistRepository;

    @PostMapping("/artists/{artistId}/posts")
    public RedirectView create(@PathVariable Long artistId, @Valid @ModelAttribute Post post, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.post", result); // result of validation checks
            redirectAttributes.addFlashAttribute("post", post); // post object the form is mapped to
            return new RedirectView("/artists/" + artistId); // Return back to the form with error messages
        }

        // if no errors, proceed with creating new post
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new RuntimeException("Artist not found"));
        post.setArtist(artist);
        postRepository.save(post);

        return new RedirectView("/artists/" + artistId);
    }

}
