package com.makers.moshpit.service;

import com.makers.moshpit.model.User;
import com.makers.moshpit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    @Autowired
    UserRepository userRepository;

    public DefaultOidcUser getAuthenticatedPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof DefaultOidcUser principal) {
            return principal;
        }

        throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "No valid authentication session found."
        );
    }

    public String getAuthenticatedUserEmail() {
        DefaultOidcUser principal = getAuthenticatedPrincipal();

        String email = principal.getEmail();
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No email on authenticated user");
        }
        return email;
    }

    public User getCurrentUser() {
        DefaultOidcUser principal = getAuthenticatedPrincipal();

        String email = principal.getEmail();
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No email on authenticated user");
        }

        return userRepository.findUserByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User(email);

                    Object nameClaim = principal.getClaims().get("name");
                    if (nameClaim instanceof String name && !name.isBlank()) {
                        newUser.setName(name);
                    }

                    Object nicknameClaim = principal.getClaims().get("nickname");
                    if (nicknameClaim instanceof String nickname && !nickname.isBlank()) {
                        newUser.setUsername(nickname);
                    }

                    Object pictureClaim = principal.getClaims().get("picture");
                    if (pictureClaim instanceof String picture && !picture.isBlank()) {
                        newUser.setAvatar(picture);
                    }

                    return userRepository.save(newUser);
                });
    }

    public User getCurrentUserOrThrow() {
        String email = getAuthenticatedUserEmail();
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User authenticated but not found in the database."
                ));
    }

    public User getCurrentUserOrNull() {
        try {
            return getCurrentUser();
        } catch (Exception e) {
            return null;
        }
    }

}

