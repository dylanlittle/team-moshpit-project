package com.makers.moshpit.spotify;

import com.makers.moshpit.model.User;
import com.makers.moshpit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CurrentUserService {

    @Autowired
    private UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getOrCreateFromPrincipal(OAuth2User principal) {
        String sub = principal.getAttribute("sub");
        String email = principal.getAttribute("email");

        return userRepository.findByAuth0Sub(sub)
                .or(() -> email != null ? userRepository.findUserByEmail(email) : Optional.empty())
                .map(u -> {
                    if (u.getAuth0Sub() == null) {
                        u.setAuth0Sub(sub);
                        userRepository.save(u);
                    }
                    return u;
                })
                .orElseGet(() -> {
                    User u = new User(email);
                    u.setAuth0Sub(sub);
                    return userRepository.save(u);
                });
    }
}
