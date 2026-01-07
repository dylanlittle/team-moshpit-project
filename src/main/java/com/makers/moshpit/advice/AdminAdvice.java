package com.makers.moshpit.advice;

import com.makers.moshpit.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class AdminAdvice {

    @Autowired
    private AdminService adminService;

    @ModelAttribute
    public void addAdminAttributes(Model model) {
        if (adminService.isInAdminMode()) {
            model.addAttribute("activeAdminArtist", adminService.getActiveAdminArtist());
            model.addAttribute("isAdminMode", true);
        } else {
            model.addAttribute("isAdminMode", false);
        }
    }
}
