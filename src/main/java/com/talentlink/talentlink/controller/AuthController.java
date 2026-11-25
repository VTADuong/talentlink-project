package com.talentlink.talentlink.controller;

import com.talentlink.talentlink.config.CustomAuthenticationFailureHandler;
import com.talentlink.talentlink.dto.StudentRegisterDto;
import com.talentlink.talentlink.dto.StudentRegistrationValidator;
import com.talentlink.talentlink.service.StudentRegistrationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller quản lý Đăng nhập (/login) và Đăng ký sinh viên (/register).
 */
@Controller
public class AuthController {

    private final StudentRegistrationService registrationService;
    private final StudentRegistrationValidator registrationValidator;

    public AuthController(
            StudentRegistrationService registrationService,
            StudentRegistrationValidator registrationValidator) {
        this.registrationService = registrationService;
        this.registrationValidator = registrationValidator;
    }

    @GetMapping("/login")
    public String loginPage(Authentication authentication, HttpSession session, Model model) {
        if (isLoggedIn(authentication)) {
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

    @GetMapping("/register")
    public String registerPage(Authentication authentication, Model model) {
        if (isLoggedIn(authentication)) {
            return "redirect:" + resolveHomeByRole(authentication);
        }
        if (!model.containsAttribute("registerDto")) {
            model.addAttribute("registerDto", new StudentRegisterDto());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(
            @Valid @ModelAttribute("registerDto") StudentRegisterDto registerDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        // Chạy thêm validator tự viết để đảm bảo kiểm tra mật khẩu khớp và email chưa dùng
        registrationValidator.validate(registerDto, bindingResult);

        if (bindingResult.hasErrors()) {
            // Trả về form kèm lỗi dưới từng trường và giữ nguyên dữ liệu người dùng đã nhập
            return "auth/register";
        }

        registrationService.registerStudent(registerDto);

        FlashMessageHelper.addSuccess(redirectAttributes,
                "Đăng ký tài khoản sinh viên thành công! Hồ sơ trống của bạn đã được tạo ở trạng thái Riêng tư. Vui lòng đăng nhập.");
        return "redirect:/login";
    }

    private boolean isLoggedIn(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
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
