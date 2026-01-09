package com.makers.moshpit.advice;

import com.makers.moshpit.model.User;
import com.makers.moshpit.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class UserAttributeAdvice {

    @Autowired
    AuthService authService;

    @ModelAttribute("currentUser")
    public User addGlobalUser() {
        try {
            return authService.getCurrentUser();
        } catch (Exception e) {
            return null;
        }
    }
}
