package com.makers.moshpit.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import com.makers.moshpit.model.User;
import com.makers.moshpit.repository.ArtistAdminRepository;
import com.makers.moshpit.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ArtistAdminAttributeAdvice {

    @Autowired
    private AuthService authService;

    @Autowired
    private ArtistAdminRepository artistAdminRepository;

    @ModelAttribute("isAdmin")
    public boolean canEdit(HttpServletRequest request) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            return false;
        }

        // Try to extract artist ID from the URL
        String path = request.getRequestURI();
        String[] parts = path.split("/");

        for (int i = 0; i < parts.length; i++) {
            if ("artists".equals(parts[i]) && i + 1 < parts.length) {
                try {
                    Long artistId = Long.parseLong(parts[i + 1]);
                    return artistAdminRepository
                            .existsByArtistIdAndUserId(artistId, currentUser.getId());
                } catch (NumberFormatException ignored) {}
            }
        }

        return false;
    }
}
