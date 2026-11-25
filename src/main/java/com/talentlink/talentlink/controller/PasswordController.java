package com.talentlink.talentlink.controller;

import com.talentlink.talentlink.dto.ChangePasswordDto;
import com.talentlink.talentlink.dto.ForgotPasswordDto;
import com.talentlink.talentlink.dto.ResetPasswordDto;
import com.talentlink.talentlink.service.PasswordService;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller xử lý Đổi mật khẩu và Quên mật khẩu bằng OTP 6 số (Task T4E7AA1).
 */
@Controller
public class PasswordController {

    private final PasswordService passwordService;

    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    // =========================================================================
    // 1. ĐỔI MẬT KHẨU (Dành cho người dùng đang đăng nhập)
    // =========================================================================

    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model) {
        if (!model.containsAttribute("changePasswordDto")) {
            model.addAttribute("changePasswordDto", new ChangePasswordDto());
        }
        return "auth/change-password";
    }

    @PostMapping("/change-password")
    public String processChangePassword(
            @Valid @ModelAttribute("changePasswordDto") ChangePasswordDto dto,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        // Kiểm tra mật khẩu mới và nhập lại mật khẩu mới khớp nhau
        if (!bindingResult.hasFieldErrors("confirmNewPassword")
                && dto.getNewPassword() != null
                && dto.getConfirmNewPassword() != null
                && !dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            bindingResult.rejectValue("confirmNewPassword", "password.mismatch",
                    "Mật khẩu mới nhập lại không khớp");
        }

        if (bindingResult.hasErrors()) {
            return "auth/change-password";
        }

        String currentEmail = authentication.getName();
        Optional<String> errorOpt = passwordService.changePassword(currentEmail, dto);
        if (errorOpt.isPresent()) {
            bindingResult.rejectValue("currentPassword", "password.incorrect", errorOpt.get());
            return "auth/change-password";
        }

        FlashMessageHelper.addSuccess(redirectAttributes,
                "Đổi mật khẩu thành công! Mật khẩu mới của bạn đã được cập nhật.");
        return "redirect:/change-password";
    }

    // =========================================================================
    // 2. QUÊN MẬT KHẨU — BƯỚC 1: Nhập Email sinh mã OTP 6 số ghi ra Log
    // =========================================================================

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        if (!model.containsAttribute("forgotPasswordDto")) {
            model.addAttribute("forgotPasswordDto", new ForgotPasswordDto());
        }
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @Valid @ModelAttribute("forgotPasswordDto") ForgotPasswordDto dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "auth/forgot-password";
        }

        Optional<String> errorOpt = passwordService.generateAndLogOtp(dto.getEmail());
        if (errorOpt.isPresent()) {
            bindingResult.rejectValue("email", "email.notFound", errorOpt.get());
            return "auth/forgot-password";
        }

        String normalizedEmail = dto.getEmail().trim().toLowerCase();
        FlashMessageHelper.addInfo(redirectAttributes,
                "Mã OTP 6 chữ số đã được sinh (hiệu lực 10 phút) và ghi ra Log Console của Server. Vui lòng xem log để nhập mã OTP.");
        return "redirect:/reset-password?email=" + normalizedEmail;
    }

    // =========================================================================
    // 3. QUÊN MẬT KHẨU — BƯỚC 2: Nhập mã OTP 6 số + Mật khẩu mới
    // =========================================================================

    @GetMapping("/reset-password")
    public String showResetPasswordForm(
            @RequestParam(value = "email", required = false) String email,
            Model model) {

        if (!model.containsAttribute("resetPasswordDto")) {
            ResetPasswordDto dto = new ResetPasswordDto();
            if (email != null && !email.isBlank()) {
                dto.setEmail(email.trim());
            }
            model.addAttribute("resetPasswordDto", dto);
        }
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(
            @Valid @ModelAttribute("resetPasswordDto") ResetPasswordDto dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (!bindingResult.hasFieldErrors("confirmNewPassword")
                && dto.getNewPassword() != null
                && dto.getConfirmNewPassword() != null
                && !dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            bindingResult.rejectValue("confirmNewPassword", "password.mismatch",
                    "Mật khẩu mới nhập lại không khớp");
        }

        if (bindingResult.hasErrors()) {
            return "auth/reset-password";
        }

        Optional<String> errorOpt = passwordService.resetPasswordWithOtp(dto);
        if (errorOpt.isPresent()) {
            bindingResult.rejectValue("otpCode", "otp.invalid", errorOpt.get());
            return "auth/reset-password";
        }

        FlashMessageHelper.addSuccess(redirectAttributes,
                "Đặt lại mật khẩu mới thành công! Vui lòng đăng nhập bằng mật khẩu mới của bạn.");
        return "redirect:/login";
    }
}
