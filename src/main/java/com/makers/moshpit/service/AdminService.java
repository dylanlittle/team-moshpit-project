package com.makers.moshpit.service;

import com.makers.moshpit.model.Artist;
import com.makers.moshpit.model.User;
import com.makers.moshpit.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private AuthService authService;

    @Autowired
    private ArtistRepository artistRepository;

    public boolean isInAdminMode() {
        User user = authService.getCurrentUser();
        return user != null && user.getActive_artist_id() != null;
    }

    public Artist getActiveAdminArtist() {
        User user = authService.getCurrentUser();
        if (user != null && user.getActive_artist_id() != null) {
            return artistRepository.findById(user.getActive_artist_id()).orElse(null);
        }
        return null;
    }
}
