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

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    UserRepository userRepository;

    public String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the security context has an authenticated user and assign to principal if so.
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof DefaultOidcUser principal) {

            String email = principal.getEmail();
            if (email == null || email.isBlank()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No email on authenticated user");
            }
            return email;
        }
        throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "No valid authentication session found."
        );
    }

    /**
     Use this when you expect current user to be authenticated AND stored in the database.
     Returns a User object or throws a 401 Unauthorized exception.
     */
    public User getCurrentUser() {

        String email = getAuthenticatedUserEmail();

        // Find the user in our DB, or throw exception if not found
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User authenticated with Okta but not found in the database."
                ));
    }
}
