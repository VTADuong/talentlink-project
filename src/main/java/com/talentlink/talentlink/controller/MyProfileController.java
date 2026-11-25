package com.talentlink.talentlink.controller;

import com.talentlink.talentlink.dto.EducationDto;
import com.talentlink.talentlink.dto.MyProfileUpdateDto;
import com.talentlink.talentlink.entity.Profile;
import com.talentlink.talentlink.entity.User;
import com.talentlink.talentlink.service.EducationService;
import com.talentlink.talentlink.service.MyProfileService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller quản lý Hồ sơ của tôi (/me/profile) và Danh sách Học vấn (/me/educations/**)
 * (Task T4E7AA3 & Task T4E7AA4).
 */
@Controller
@RequestMapping("/me")
public class MyProfileController {

    private final MyProfileService myProfileService;
    private final EducationService educationService;

    public MyProfileController(MyProfileService myProfileService, EducationService educationService) {
        this.myProfileService = myProfileService;
        this.educationService = educationService;
    }

    @GetMapping("/profile")
    public String viewAndEditProfile(Authentication authentication, Model model) {
        String currentEmail = authentication.getName();
        populateProfileAndEducations(currentEmail, model);

        if (!model.containsAttribute("profileDto")) {
            model.addAttribute("profileDto", myProfileService.buildFormDto(currentEmail));
        }
        if (!model.containsAttribute("educationDto")) {
            model.addAttribute("educationDto", new EducationDto());
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

        if (!bindingResult.hasFieldErrors("dateOfBirth") && profileDto.getDateOfBirth() != null) {
            LocalDate dob = profileDto.getDateOfBirth();
            if (dob.isBefore(LocalDate.of(1950, 1, 1)) || dob.isAfter(LocalDate.now().minusYears(15))) {
                bindingResult.rejectValue("dateOfBirth", "dateOfBirth.range",
                        "Ngày sinh không hợp lệ (ứng viên phải từ 15 tuổi trở lên)");
            }
        }

        if (bindingResult.hasErrors()) {
            populateProfileAndEducations(currentEmail, model);
            model.addAttribute("educationDto", new EducationDto());
            return "me/profile";
        }

        myProfileService.updateMyProfile(currentEmail, profileDto);
        FlashMessageHelper.addSuccess(redirectAttributes,
                "Cập nhật thông tin Hồ sơ của tôi thành công!");
        return "redirect:/me/profile";
    }

    // =========================================================================
    // QUẢN LÝ HỌC VẤN TRÊN TRANG HỒ SƠ (Task T4E7AA4)
    // =========================================================================

    /**
     * Mở form sửa một bản ghi học vấn theo ID trên URL.
     * Nếu ID là học vấn của người khác -> Chặn 403 Forbidden.
     */
    @GetMapping("/educations/{id}/edit")
    public String editEducationForm(
            @PathVariable("id") Long id,
            Authentication authentication,
            Model model) {

        String currentEmail = authentication.getName();
        EducationDto editDto = educationService.getMyEducationDtoForEdit(id, currentEmail);

        populateProfileAndEducations(currentEmail, model);
        model.addAttribute("profileDto", myProfileService.buildFormDto(currentEmail));
        model.addAttribute("educationDto", editDto);
        model.addAttribute("editingEducation", true);
        return "me/profile";
    }

    /**
     * Thêm mới học vấn vào hồ sơ của tôi.
     */
    @PostMapping("/educations")
    public String createEducation(
            @Valid @ModelAttribute("educationDto") EducationDto educationDto,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        String currentEmail = authentication.getName();
        validateStartAndEndDate(educationDto, bindingResult);

        if (bindingResult.hasErrors()) {
            populateProfileAndEducations(currentEmail, model);
            model.addAttribute("profileDto", myProfileService.buildFormDto(currentEmail));
            return "me/profile";
        }

        educationService.createEducation(currentEmail, educationDto);
        FlashMessageHelper.addSuccess(redirectAttributes,
                "Đã thêm thông tin học vấn [" + educationDto.getSchoolName().trim() + "] thành công!");
        return "redirect:/me/profile";
    }

    /**
     * Lưu cập nhật học vấn theo ID trên URL.
     * Nếu đổi ID trên URL sang học vấn của người khác -> Chặn 403 Forbidden.
     */
    @PostMapping("/educations/{id}")
    public String updateEducation(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("educationDto") EducationDto educationDto,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        String currentEmail = authentication.getName();
        educationDto.setId(id);
        validateStartAndEndDate(educationDto, bindingResult);

        if (bindingResult.hasErrors()) {
            // Vẫn kiểm tra quyền sở hữu trước khi render lại form
            educationService.getMyEducationDtoForEdit(id, currentEmail);
            populateProfileAndEducations(currentEmail, model);
            model.addAttribute("profileDto", myProfileService.buildFormDto(currentEmail));
            model.addAttribute("editingEducation", true);
            return "me/profile";
        }

        educationService.updateEducation(id, currentEmail, educationDto);
        FlashMessageHelper.addSuccess(redirectAttributes,
                "Đã cập nhật thông tin học vấn [" + educationDto.getSchoolName().trim() + "] thành công!");
        return "redirect:/me/profile";
    }

    /**
     * Xóa học vấn theo ID trên URL.
     * Nếu đổi ID trên URL sang học vấn của người khác -> Chặn 403 Forbidden.
     */
    @PostMapping("/educations/{id}/delete")
    public String deleteEducation(
            @PathVariable("id") Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        String currentEmail = authentication.getName();
        educationService.deleteEducation(id, currentEmail);
        FlashMessageHelper.addSuccess(redirectAttributes,
                "Đã xóa thông tin học vấn thành công!");
        return "redirect:/me/profile";
    }

    private void validateStartAndEndDate(EducationDto dto, BindingResult bindingResult) {
        if (dto.getStartDate() != null && dto.getEndDate() != null) {
            if (dto.getStartDate().isAfter(dto.getEndDate())) {
                bindingResult.rejectValue("endDate", "education.dateRange",
                        "Ngày bắt đầu không được sau (lớn hơn) ngày kết thúc");
            }
        }
    }

    private void populateProfileAndEducations(String currentEmail, Model model) {
        User currentUser = myProfileService.getCurrentUser(currentEmail);
        Profile myProfile = myProfileService.getOrCreateMyProfile(currentEmail);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("myProfile", myProfile);
        model.addAttribute("educations", educationService.listMyEducations(currentEmail));
    }
}
