package com.makers.moshpit.advice;


import com.makers.moshpit.model.Artist;
import com.makers.moshpit.model.User;
import com.makers.moshpit.repository.ArtistRepository;
import com.makers.moshpit.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class UserArtistsAdvice {

    @Autowired
    AuthService authService;

    @Autowired
    ArtistRepository artistRepository;

    @ModelAttribute("myArtists")
    public void addUserArtists(Model model) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            model.addAttribute("myArtists", List.of());
            return;
        }
        List<Artist> myArtists =  artistRepository.findArtistsByUserId(currentUser.getId());

        model.addAttribute("myArtists", myArtists);
    }

}
