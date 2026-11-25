package com.talentlink.talentlink.controller;

import com.talentlink.talentlink.dto.AddProfileSkillDto;
import com.talentlink.talentlink.dto.EducationDto;
import com.talentlink.talentlink.dto.MyProfileUpdateDto;
import com.talentlink.talentlink.entity.Profile;
import com.talentlink.talentlink.entity.Skill;
import com.talentlink.talentlink.entity.User;
import com.talentlink.talentlink.service.EducationService;
import com.talentlink.talentlink.service.MyProfileService;
import com.talentlink.talentlink.service.MySkillService;
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
 * Controller quản lý Hồ sơ của tôi (/me/profile), Học vấn (/me/educations/**)
 * và Kỹ năng của tôi (/me/skills/**) (Task T4E7AA3, T4E7AA4, T4E7AA6).
 */
@Controller
@RequestMapping("/me")
public class MyProfileController {

    private final MyProfileService myProfileService;
    private final EducationService educationService;
    private final MySkillService mySkillService;

    public MyProfileController(
            MyProfileService myProfileService,
            EducationService educationService,
            MySkillService mySkillService) {
        this.myProfileService = myProfileService;
        this.educationService = educationService;
        this.mySkillService = mySkillService;
    }

    @GetMapping("/profile")
    public String viewAndEditProfile(Authentication authentication, Model model) {
        String currentEmail = authentication.getName();
        populateProfileData(currentEmail, model);

        if (!model.containsAttribute("profileDto")) {
            model.addAttribute("profileDto", myProfileService.buildFormDto(currentEmail));
        }
        if (!model.containsAttribute("educationDto")) {
            model.addAttribute("educationDto", new EducationDto());
        }
        if (!model.containsAttribute("addSkillDto")) {
            model.addAttribute("addSkillDto", new AddProfileSkillDto());
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
            populateProfileData(currentEmail, model);
            model.addAttribute("educationDto", new EducationDto());
            model.addAttribute("addSkillDto", new AddProfileSkillDto());
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

    @GetMapping("/educations/{id}/edit")
    public String editEducationForm(
            @PathVariable("id") Long id,
            Authentication authentication,
            Model model) {

        String currentEmail = authentication.getName();
        EducationDto editDto = educationService.getMyEducationDtoForEdit(id, currentEmail);

        populateProfileData(currentEmail, model);
        model.addAttribute("profileDto", myProfileService.buildFormDto(currentEmail));
        model.addAttribute("educationDto", editDto);
        model.addAttribute("addSkillDto", new AddProfileSkillDto());
        model.addAttribute("editingEducation", true);
        return "me/profile";
    }

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
            populateProfileData(currentEmail, model);
            model.addAttribute("profileDto", myProfileService.buildFormDto(currentEmail));
            model.addAttribute("addSkillDto", new AddProfileSkillDto());
            return "me/profile";
        }

        educationService.createEducation(currentEmail, educationDto);
        FlashMessageHelper.addSuccess(redirectAttributes,
                "Đã thêm thông tin học vấn [" + educationDto.getSchoolName().trim() + "] thành công!");
        return "redirect:/me/profile";
    }

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
            educationService.getMyEducationDtoForEdit(id, currentEmail);
            populateProfileData(currentEmail, model);
            model.addAttribute("profileDto", myProfileService.buildFormDto(currentEmail));
            model.addAttribute("addSkillDto", new AddProfileSkillDto());
            model.addAttribute("editingEducation", true);
            return "me/profile";
        }

        educationService.updateEducation(id, currentEmail, educationDto);
        FlashMessageHelper.addSuccess(redirectAttributes,
                "Đã cập nhật thông tin học vấn [" + educationDto.getSchoolName().trim() + "] thành công!");
        return "redirect:/me/profile";
    }

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

    // =========================================================================
    // QUẢN LÝ KỸ NĂNG CỦA TÔI TRÊN TRANG HỒ SƠ (Task T4E7AA6)
    // =========================================================================

    @PostMapping("/skills")
    public String addSkillToProfile(
            @Valid @ModelAttribute("addSkillDto") AddProfileSkillDto addSkillDto,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        String currentEmail = authentication.getName();

        // Kiểm tra không cho phép chọn trùng kỹ năng
        if (!bindingResult.hasFieldErrors("skillId")
                && mySkillService.isDuplicateSkillInProfile(currentEmail, addSkillDto.getSkillId())) {
            bindingResult.rejectValue("skillId", "skill.duplicate",
                    "Kỹ năng này đã có trong hồ sơ của bạn. Vui lòng không chọn trùng kỹ năng!");
        }

        if (bindingResult.hasErrors()) {
            populateProfileData(currentEmail, model);
            model.addAttribute("profileDto", myProfileService.buildFormDto(currentEmail));
            model.addAttribute("educationDto", new EducationDto());
            return "me/profile";
        }

        Skill added = mySkillService.addSkillToMyProfile(currentEmail, addSkillDto);
        FlashMessageHelper.addSuccess(redirectAttributes,
                String.format("Đã thêm kỹ năng [%s] (Nhóm: %s — Mức %d/5) vào hồ sơ của bạn!",
                        added.getName(), added.getCategory(), addSkillDto.getProficiencyLevel()));
        return "redirect:/me/profile";
    }

    @PostMapping("/skills/{id}/delete")
    public String removeSkillFromProfile(
            @PathVariable("id") Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        String currentEmail = authentication.getName();
        mySkillService.removeSkillFromMyProfile(id, currentEmail);
        FlashMessageHelper.addSuccess(redirectAttributes,
                "Đã bỏ kỹ năng khỏi hồ sơ của bạn thành công!");
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

    private void populateProfileData(String currentEmail, Model model) {
        User currentUser = myProfileService.getCurrentUser(currentEmail);
        Profile myProfile = myProfileService.getOrCreateMyProfile(currentEmail);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("myProfile", myProfile);
        model.addAttribute("educations", educationService.listMyEducations(currentEmail));
        model.addAttribute("groupedSkills", mySkillService.getMySkillsGroupedByCategory(currentEmail));
        model.addAttribute("availableSkills", mySkillService.listAvailableCatalogSkills());
        model.addAttribute("selectedSkillIds", mySkillService.getMySelectedSkillIds(currentEmail));
        model.addAttribute("levelDescriptions", MySkillService.LEVEL_DESCRIPTIONS);
    }
}
