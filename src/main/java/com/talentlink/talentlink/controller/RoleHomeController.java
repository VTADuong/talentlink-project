package com.talentlink.talentlink.controller;

import com.talentlink.talentlink.entity.Profile;
import com.talentlink.talentlink.repository.ProfileRepository;
import com.talentlink.talentlink.service.CustomUserPrincipal;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller trang chủ riêng cho từng vai trò sau khi đăng nhập:
 * - /admin/**   : Chỉ dành cho ADMIN
 * - /partner/** : Chỉ dành cho PARTNER
 * - /me/**      : Chỉ dành cho STUDENT
 */
@Controller
public class RoleHomeController {

    private final ProfileRepository profileRepository;

    public RoleHomeController(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @GetMapping({"/admin", "/admin/dashboard"})
    public String adminHome(Authentication authentication, Model model) {
        model.addAttribute("currentUsername", authentication != null ? authentication.getName() : "admin@talentlink.demo");
        model.addAttribute("roleLabel", "ADMIN (Quản trị viên)");
        return "dashboard/admin-home";
    }

    @GetMapping({"/partner", "/partner/dashboard"})
    public String partnerHome(Authentication authentication, Model model) {
        model.addAttribute("currentUsername", authentication != null ? authentication.getName() : "partner1@talentlink.demo");
        model.addAttribute("roleLabel", "PARTNER (Đối tác / Nhà tuyển dụng)");
        return "dashboard/partner-home";
    }

    @GetMapping({"/me", "/me/dashboard"})
    public String studentHome(Authentication authentication, Model model) {
        model.addAttribute("currentUsername", authentication != null ? authentication.getName() : "student01@talentlink.demo");
        model.addAttribute("roleLabel", "STUDENT (Sinh viên / Ứng viên)");

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserPrincipal principal) {
            model.addAttribute("fullName", principal.getFullName());
            Optional<Profile> profileOpt = profileRepository.findByUserId(principal.getId());
            profileOpt.ifPresent(profile -> model.addAttribute("studentProfile", profile));
        }

        return "dashboard/student-home";
    }
}
