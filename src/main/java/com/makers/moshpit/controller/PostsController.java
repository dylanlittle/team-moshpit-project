package com.makers.moshpit.controller;

import com.makers.moshpit.model.Artist;
import com.makers.moshpit.model.Post;
import com.makers.moshpit.repository.ArtistRepository;
import com.makers.moshpit.repository.PostRepository;
import com.makers.moshpit.service.MediaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Objects;

@Controller
public class PostsController {
    @Autowired
    PostRepository postRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    MediaService mediaService;

    @PostMapping("/artists/{artistId}/posts")
    public RedirectView create(@PathVariable Long artistId,
                               @Valid @ModelAttribute Post post,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               @RequestParam(value = "media_file", required = false) MultipartFile mediaFile) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.post", result); // result of validation checks
            redirectAttributes.addFlashAttribute("post", post); // post object the form is mapped to
            return new RedirectView("/artists/" + artistId); // Return back to the form with error messages
        }

        if (mediaFile != null && !mediaFile.isEmpty()) {
            String contentType = mediaFile.getContentType();
            if (contentType != null && contentType.startsWith("audio")) {
                post.setMediaType("audio");
                if (mediaFile.getSize() > (256 * 1024 * 1024)) {
                    throw new RuntimeException("File too large — maximum allowed size is 256MB.");
                }
                String mediaUrl = mediaService.uploadVideoOrAudio(mediaFile);
                post.setMediaUrl(mediaUrl);
            } else if (contentType != null && contentType.startsWith("video")) {
                post.setMediaType("video");
                if (mediaFile.getSize() > (256 * 1024 * 1024)) {
                    throw new RuntimeException("File too large — maximum allowed size is 256MB.");
                }
                String mediaUrl = mediaService.uploadVideoOrAudio(mediaFile);
                post.setMediaUrl(mediaUrl);
            } else {
                post.setMediaType("image");
                if (mediaFile.getSize() > (10 * 1024 * 1024)) {
                    throw new RuntimeException("File too large — maximum allowed size is 10MB.");
                }
                String mediaUrl = mediaService.uploadImage(mediaFile);
                post.setMediaUrl(mediaUrl);
            }
        }

        // if no errors, proceed with creating new post
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new RuntimeException("Artist not found"));
        post.setArtist(artist);
        postRepository.save(post);

        return new RedirectView("/artists/" + artistId);
    }

}
