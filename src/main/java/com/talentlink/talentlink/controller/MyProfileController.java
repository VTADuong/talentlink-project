package com.talentlink.talentlink.controller;

import com.talentlink.talentlink.dto.AddProfileSkillDto;
import com.talentlink.talentlink.dto.CertificateDto;
import com.talentlink.talentlink.dto.EducationDto;
import com.talentlink.talentlink.dto.MyProfileUpdateDto;
import com.talentlink.talentlink.dto.ProjectDto;
import com.talentlink.talentlink.entity.Certificate;
import com.talentlink.talentlink.entity.Profile;
import com.talentlink.talentlink.entity.Project;
import com.talentlink.talentlink.entity.Skill;
import com.talentlink.talentlink.entity.User;
import com.talentlink.talentlink.service.AvatarService;
import com.talentlink.talentlink.service.CertificateService;
import com.talentlink.talentlink.service.EducationService;
import com.talentlink.talentlink.service.MyProfileService;
import com.talentlink.talentlink.service.MySkillService;
import com.talentlink.talentlink.service.ProjectService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller quản lý Hồ sơ của tôi (/me/profile), Ảnh đại diện (/me/profile/avatar),
 * Học vấn (/me/educations/**), Kỹ năng của tôi (/me/skills/**), Dự án cá nhân (/me/projects/**)
 * và Chứng chỉ & File minh chứng (/me/certificates/**)
 * (Task T4E7AA3, T4E7AA4, T4E7AA6, T4E7AA7, T4E7AA8, T4E7AA9).
 */
@Controller
@RequestMapping("/me")
public class MyProfileController {

    private final MyProfileService myProfileService;
    private final EducationService educationService;
    private final MySkillService mySkillService;
    private final ProjectService projectService;
    private final CertificateService certificateService;
    private final AvatarService avatarService;

    public MyProfileController(
            MyProfileService myProfileService,
            EducationService educationService,
            MySkillService mySkillService,
            ProjectService projectService,
            CertificateService certificateService,
            AvatarService avatarService) {
        this.myProfileService = myProfileService;
        this.educationService = educationService;
        this.mySkillService = mySkillService;
        this.projectService = projectService;
        this.certificateService = certificateService;
        this.avatarService = avatarService;
    }

    @GetMapping("/profile")
    public String viewAndEditProfile(Authentication authentication, Model model) {
        String currentEmail = authentication.getName();
        populateProfileData(currentEmail, model);
        ensureDefaultFormDtos(currentEmail, model);
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
            ensureDefaultFormDtos(currentEmail, model);
            return "me/profile";
        }

        myProfileService.updateMyProfile(currentEmail, profileDto);
        FlashMessageHelper.addSuccess(redirectAttributes,
                "Cập nhật thông tin Hồ sơ của tôi thành công!");
        return "redirect:/me/profile";
    }

    // =========================================================================
    // QUẢN LÝ ẢNH ĐẠI DIỆN TRÊN TRANG HỒ SƠ (Task T4E7AA9)
    // =========================================================================

    @PostMapping("/profile/avatar")
    public String uploadAvatar(
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        String currentEmail = authentication.getName();
        Optional<String> validationError = avatarService.validateAvatarFile(avatarFile);

        if (validationError.isPresent()) {
            model.addAttribute("avatarError", validationError.get());
            populateProfileData(currentEmail, model);
            ensureDefaultFormDtos(currentEmail, model);
            return "me/profile";
        }

        avatarService.updateMyAvatar(currentEmail, avatarFile);
        FlashMessageHelper.addSuccess(redirectAttributes,
                "Đã tải lên và lưu ảnh đại diện mới thành công (ảnh cũ đã được xóa tự động)!");
        return "redirect:/me/profile";
    }

    @PostMapping("/profile/avatar/delete")
    public String deleteAvatar(
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        String currentEmail = authentication.getName();
        avatarService.deleteMyAvatar(currentEmail);
        FlashMessageHelper.addSuccess(redirectAttributes,
                "Đã xóa ảnh đại diện cũ và chuyển về ảnh mặc định theo chữ cái đầu tên!");
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
        model.addAttribute("educationDto", editDto);
        model.addAttribute("editingEducation", true);
        ensureDefaultFormDtos(currentEmail, model);
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
            ensureDefaultFormDtos(currentEmail, model);
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
            model.addAttribute("editingEducation", true);
            ensureDefaultFormDtos(currentEmail, model);
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

        if (!bindingResult.hasFieldErrors("skillId")
                && mySkillService.isDuplicateSkillInProfile(currentEmail, addSkillDto.getSkillId())) {
            bindingResult.rejectValue("skillId", "skill.duplicate",
                    "Kỹ năng này đã có trong hồ sơ của bạn. Vui lòng không chọn trùng kỹ năng!");
        }

        if (bindingResult.hasErrors()) {
            populateProfileData(currentEmail, model);
            ensureDefaultFormDtos(currentEmail, model);
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

    // =========================================================================
    // QUẢN LÝ DỰ ÁN CÁ NHÂN TRÊN TRANG HỒ SƠ (Task T4E7AA7)
    // =========================================================================

    @GetMapping("/projects/{id}/edit")
    public String editProjectForm(
            @PathVariable("id") Long id,
            Authentication authentication,
            Model model) {

        String currentEmail = authentication.getName();
        ProjectDto editDto = projectService.getMyProjectDtoForEdit(id, currentEmail);

        populateProfileData(currentEmail, model);
        model.addAttribute("projectDto", editDto);
        model.addAttribute("editingProject", true);
        ensureDefaultFormDtos(currentEmail, model);
        return "me/profile";
    }

    @PostMapping("/projects")
    public String createProject(
            @Valid @ModelAttribute("projectDto") ProjectDto projectDto,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        String currentEmail = authentication.getName();

        if (projectService.hasReachedMaxProjects(currentEmail)) {
            bindingResult.rejectValue("projectName", "project.limitReached",
                    "Hồ sơ của bạn đã đạt giới hạn tối đa " + ProjectService.MAX_PROJECTS_PER_PROFILE + " dự án!");
        }

        if (bindingResult.hasErrors()) {
            populateProfileData(currentEmail, model);
            ensureDefaultFormDtos(currentEmail, model);
            return "me/profile";
        }

        Project created = projectService.createProject(currentEmail, projectDto);
        FlashMessageHelper.addSuccess(redirectAttributes,
                "Đã thêm dự án cá nhân [" + created.getProjectName() + "] vào hồ sơ thành công!");
        return "redirect:/me/profile";
    }

    @PostMapping("/projects/{id}")
    public String updateProject(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("projectDto") ProjectDto projectDto,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        String currentEmail = authentication.getName();
        projectDto.setId(id);

        if (bindingResult.hasErrors()) {
            projectService.getMyProjectDtoForEdit(id, currentEmail);
            populateProfileData(currentEmail, model);
            model.addAttribute("editingProject", true);
            ensureDefaultFormDtos(currentEmail, model);
            return "me/profile";
        }

        Project updated = projectService.updateProject(id, currentEmail, projectDto);
        FlashMessageHelper.addSuccess(redirectAttributes,
                "Đã cập nhật dự án cá nhân [" + updated.getProjectName() + "] thành công!");
        return "redirect:/me/profile";
    }

    @PostMapping("/projects/{id}/delete")
    public String deleteProject(
            @PathVariable("id") Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        String currentEmail = authentication.getName();
        projectService.deleteProject(id, currentEmail);
        FlashMessageHelper.addSuccess(redirectAttributes,
                "Đã xóa dự án cá nhân khỏi hồ sơ thành công!");
        return "redirect:/me/profile";
    }

    // =========================================================================
    // QUẢN LÝ CHỨNG CHỈ & TẢI FILE MINH CHỨNG (Task T4E7AA8)
    // =========================================================================

    @PostMapping("/certificates")
    public String createCertificate(
            @Valid @ModelAttribute("certificateDto") CertificateDto certificateDto,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        String currentEmail = authentication.getName();

        // Kiểm tra file minh chứng: PDF/JPG/PNG, tối đa 5MB (kiểm cả phần mở rộng và Content-Type)
        Optional<String> fileError = certificateService.validateEvidenceFile(certificateDto.getEvidenceFile());
        if (fileError.isPresent()) {
            bindingResult.rejectValue("evidenceFile", "certificate.file.invalid", fileError.get());
        }

        if (bindingResult.hasErrors()) {
            populateProfileData(currentEmail, model);
            ensureDefaultFormDtos(currentEmail, model);
            return "me/profile";
        }

        Certificate created = certificateService.createCertificate(currentEmail, certificateDto);
        FlashMessageHelper.addSuccess(redirectAttributes,
                String.format("Đã thêm chứng chỉ [%s] kèm file minh chứng [%s] thành công!",
                        created.getCertificateName(), created.getOriginalFileName()));
        return "redirect:/me/profile";
    }

    @PostMapping("/certificates/{id}/delete")
    public String deleteCertificate(
            @PathVariable("id") Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        String currentEmail = authentication.getName();
        certificateService.deleteCertificateAndFile(id, currentEmail);
        FlashMessageHelper.addSuccess(redirectAttributes,
                "Đã xóa chứng chỉ và xóa file minh chứng trên hệ thống thành công!");
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

    private void ensureDefaultFormDtos(String currentEmail, Model model) {
        if (!model.containsAttribute("profileDto")) {
            model.addAttribute("profileDto", myProfileService.buildFormDto(currentEmail));
        }
        if (!model.containsAttribute("educationDto")) {
            model.addAttribute("educationDto", new EducationDto());
        }
        if (!model.containsAttribute("addSkillDto")) {
            model.addAttribute("addSkillDto", new AddProfileSkillDto());
        }
        if (!model.containsAttribute("projectDto")) {
            model.addAttribute("projectDto", new ProjectDto());
        }
        if (!model.containsAttribute("certificateDto")) {
            model.addAttribute("certificateDto", new CertificateDto());
        }
    }

    private void populateProfileData(String currentEmail, Model model) {
        User currentUser = myProfileService.getCurrentUser(currentEmail);
        Profile myProfile = myProfileService.getOrCreateMyProfile(currentEmail);
        List<Project> projects = projectService.listMyProjects(currentEmail);
        List<Certificate> certificates = certificateService.listMyCertificates(currentEmail);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("myProfile", myProfile);
        model.addAttribute("avatarInitial",
                avatarService.extractInitialFromName(currentUser != null ? currentUser.getFullName() : null));
        model.addAttribute("educations", educationService.listMyEducations(currentEmail));
        model.addAttribute("groupedSkills", mySkillService.getMySkillsGroupedByCategory(currentEmail));
        model.addAttribute("availableSkills", mySkillService.listAvailableCatalogSkills());
        model.addAttribute("selectedSkillIds", mySkillService.getMySelectedSkillIds(currentEmail));
        model.addAttribute("levelDescriptions", MySkillService.LEVEL_DESCRIPTIONS);
        model.addAttribute("projects", projects);
        model.addAttribute("projectCount", projects.size());
        model.addAttribute("maxProjects", ProjectService.MAX_PROJECTS_PER_PROFILE);
        model.addAttribute("certificates", certificates);
    }
}
