package com.talentlink.talentlink.controller;

import com.talentlink.talentlink.dto.MyProfileUpdateDto;
import com.talentlink.talentlink.entity.Profile;
import com.talentlink.talentlink.entity.User;
import com.talentlink.talentlink.service.MyProfileService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller cho trang Hồ sơ của tôi tại /me/profile (Task T4E7AA3):
 * - Xem và sửa: họ tên, ngày sinh, số điện thoại, giới thiệu bản thân,
 *   vị trí mong muốn, nơi làm việc mong muốn.
 * - Validate định dạng ngày và số điện thoại, hiện lỗi từng trường.
 * - Chỉ chủ hồ sơ mới sửa được.
 */
@Controller
@RequestMapping("/me")
public class MyProfileController {

    private final MyProfileService myProfileService;

    public MyProfileController(MyProfileService myProfileService) {
        this.myProfileService = myProfileService;
    }

    @GetMapping("/profile")
    public String viewAndEditProfile(Authentication authentication, Model model) {
        String currentEmail = authentication.getName();
        User currentUser = myProfileService.getCurrentUser(currentEmail);
        Profile myProfile = myProfileService.getOrCreateMyProfile(currentEmail);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("myProfile", myProfile);
        if (!model.containsAttribute("profileDto")) {
            model.addAttribute("profileDto", myProfileService.buildFormDto(currentEmail));
        }
        return "me/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @Valid @ModelAttribute("profileDto") MyProfileUpdateDto profileDto,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        String currentEmail = authentication.getName();

        // Kiểm tra bổ sung khoảng ngày sinh hợp lệ (từ năm 1950 đến trước ngày hiện tại ít nhất 15 năm)
        if (!bindingResult.hasFieldErrors("dateOfBirth") && profileDto.getDateOfBirth() != null) {
            LocalDate dob = profileDto.getDateOfBirth();
            if (dob.isBefore(LocalDate.of(1950, 1, 1)) || dob.isAfter(LocalDate.now().minusYears(15))) {
                bindingResult.rejectValue("dateOfBirth", "dateOfBirth.range",
                        "Ngày sinh không hợp lệ (ứng viên phải từ 15 tuổi trở lên)");
            }
        }

        if (bindingResult.hasErrors()) {
            User currentUser = myProfileService.getCurrentUser(currentEmail);
            Profile myProfile = myProfileService.getOrCreateMyProfile(currentEmail);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("myProfile", myProfile);
            return "me/profile";
        }

        myProfileService.updateMyProfile(currentEmail, profileDto);
        FlashMessageHelper.addSuccess(redirectAttributes,
                "Cập nhật thông tin Hồ sơ của tôi thành công!");
        return "redirect:/me/profile";
    }
}
