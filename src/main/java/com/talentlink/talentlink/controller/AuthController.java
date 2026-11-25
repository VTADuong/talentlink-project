package com.talentlink.talentlink.controller;

import com.talentlink.talentlink.config.CustomAuthenticationFailureHandler;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller quản lý trang đăng nhập riêng (/login).
 */
@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage(Authentication authentication, HttpSession session, Model model) {
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:" + resolveHomeByRole(authentication);
        }

        Object sessionError = session.getAttribute(CustomAuthenticationFailureHandler.AUTH_ERROR_SESSION_KEY);
        if (sessionError != null) {
            model.addAttribute("authErrorMessage", sessionError.toString());
        }

        Object lastEmail = session.getAttribute(CustomAuthenticationFailureHandler.LAST_EMAIL_SESSION_KEY);
        if (lastEmail != null) {
            model.addAttribute("lastEmail", lastEmail.toString());
        }

        return "auth/login";
    }

    private String resolveHomeByRole(Authentication authentication) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            if ("ROLE_ADMIN".equals(role)) {
                return "/admin/dashboard";
            }
            if ("ROLE_PARTNER".equals(role)) {
                return "/partner/dashboard";
            }
            if ("ROLE_STUDENT".equals(role)) {
                return "/me/dashboard";
            }
        }
        return "/";
    }
}
