package com.makers.moshpit.controller;

import com.makers.moshpit.model.Artist;
import com.makers.moshpit.model.User;
import com.makers.moshpit.repository.ArtistRepository;
import com.makers.moshpit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@Controller
public class SearchController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @GetMapping("/search")
    public String searchAll(@RequestParam(name = "query", required = false) String query,
                            @RequestParam(name = "type", required = false) String type,
                              Model model) {
        if (query == null || query.trim().isEmpty()) {
            model.addAttribute("query", "");
            model.addAttribute("artistResults", java.util.Collections.emptyList());
            model.addAttribute("userResults", java.util.Collections.emptyList());
            model.addAttribute("suggestedResults", java.util.Collections.emptyList());
            model.addAttribute("type", "all");
            return "/search_results";
        }

        String trimmedQuery = query.trim();
        List<Artist> artistResults = java.util.Collections.emptyList();
        List<User> userResults = java.util.Collections.emptyList();

        String resolvedType = (type == null) ? "all" : type;

        switch (resolvedType) {
            case "users":
                userResults = userRepository.findByUsernameContainingIgnoreCase(trimmedQuery);
                break;
            case "artists":
                artistResults = artistRepository.findByNameContainingIgnoreCase(trimmedQuery);
            break;
            case "all":
            default:
                artistResults = artistRepository.findByNameContainingIgnoreCase(trimmedQuery);
                userResults = userRepository.findByUsernameContainingIgnoreCase(trimmedQuery);
                break;
        }

        model.addAttribute("query", query);
        model.addAttribute("artistResults", artistResults);
        model.addAttribute("userResults", userResults);
        model.addAttribute("type", resolvedType);
        return "search_results";
    }

}
