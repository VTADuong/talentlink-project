package com.talentlink.talentlink.controller;

import com.talentlink.talentlink.dto.SkillDto;
import com.talentlink.talentlink.entity.Skill;
import com.talentlink.talentlink.service.AdminSkillService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller Quản trị danh mục kỹ năng tại /admin/skills (Task T4E7AA5):
 * - Thêm, sửa, ẩn kỹ năng.
 * - Nhóm kỹ năng: Ngôn ngữ, Framework, CSDL, Công cụ, Kỹ năng mềm.
 * - Tên không trùng, không phân biệt hoa thường; trùng thì báo lỗi trên form.
 * - Kỹ năng đã có sinh viên dùng thì chỉ ẩn, không xóa.
 */
@Controller
@RequestMapping("/admin/skills")
public class AdminSkillController {

    private final AdminSkillService adminSkillService;

    public AdminSkillController(AdminSkillService adminSkillService) {
        this.adminSkillService = adminSkillService;
    }

    @GetMapping
    public String listSkills(
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "category", defaultValue = "ALL") String category,
            @RequestParam(value = "status", defaultValue = "ALL") String status,
            Authentication authentication,
            Model model) {

        populateSkillListModel(keyword, category, status, authentication, model);
        if (!model.containsAttribute("skillDto")) {
            SkillDto defaultDto = new SkillDto();
            defaultDto.setCategory(AdminSkillService.SKILL_CATEGORIES.get(0));
            model.addAttribute("skillDto", defaultDto);
        }
        return "admin/skills";
    }

    @GetMapping("/{id}/edit")
    public String editSkillForm(
            @PathVariable("id") Long id,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "category", defaultValue = "ALL") String category,
            @RequestParam(value = "status", defaultValue = "ALL") String status,
            Authentication authentication,
            Model model) {

        SkillDto editDto = adminSkillService.getSkillDtoById(id);
        populateSkillListModel(keyword, category, status, authentication, model);
        model.addAttribute("skillDto", editDto);
        model.addAttribute("editingSkill", true);
        return "admin/skills";
    }

    @PostMapping
    public String createSkill(
            @Valid @ModelAttribute("skillDto") SkillDto skillDto,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (!bindingResult.hasFieldErrors("name")
                && adminSkillService.isDuplicateSkillName(skillDto.getName(), null)) {
            bindingResult.rejectValue("name", "skill.duplicate",
                    "Tên kỹ năng '" + skillDto.getName().trim() + "' đã tồn tại (không phân biệt hoa thường)");
        }

        if (bindingResult.hasErrors()) {
            populateSkillListModel("", "ALL", "ALL", authentication, model);
            return "admin/skills";
        }

        Skill created = adminSkillService.createSkill(skillDto);
        FlashMessageHelper.addSuccess(redirectAttributes,
                String.format("Đã thêm kỹ năng [%s] vào nhóm [%s] thành công!",
                        created.getName(), created.getCategory()));
        return "redirect:/admin/skills";
    }

    @PostMapping("/{id}")
    public String updateSkill(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("skillDto") SkillDto skillDto,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        skillDto.setId(id);
        if (!bindingResult.hasFieldErrors("name")
                && adminSkillService.isDuplicateSkillName(skillDto.getName(), id)) {
            bindingResult.rejectValue("name", "skill.duplicate",
                    "Tên kỹ năng '" + skillDto.getName().trim() + "' đã tồn tại (không phân biệt hoa thường)");
        }

        if (bindingResult.hasErrors()) {
            populateSkillListModel("", "ALL", "ALL", authentication, model);
            model.addAttribute("editingSkill", true);
            return "admin/skills";
        }

        Skill updated = adminSkillService.updateSkill(id, skillDto);
        FlashMessageHelper.addSuccess(redirectAttributes,
                String.format("Đã cập nhật kỹ năng [%s] (Nhóm: %s) thành công!",
                        updated.getName(), updated.getCategory()));
        return "redirect:/admin/skills";
    }

    @PostMapping("/{id}/toggle-hidden")
    public String toggleHidden(
            @PathVariable("id") Long id,
            RedirectAttributes redirectAttributes) {

        Skill toggled = adminSkillService.toggleHidden(id);
        if (toggled.isHidden()) {
            FlashMessageHelper.addWarning(redirectAttributes,
                    String.format("Đã chuyển kỹ năng [%s] sang trạng thái ẨN.", toggled.getName()));
        } else {
            FlashMessageHelper.addSuccess(redirectAttributes,
                    String.format("Đã hiển thị lại kỹ năng [%s].", toggled.getName()));
        }
        return "redirect:/admin/skills";
    }

    @PostMapping("/{id}/delete")
    public String deleteSkill(
            @PathVariable("id") Long id,
            RedirectAttributes redirectAttributes) {

        SkillDto skillInfo = adminSkillService.getSkillDtoById(id);
        boolean hardDeleted = adminSkillService.deleteOrHideIfUsed(id);

        if (hardDeleted) {
            FlashMessageHelper.addSuccess(redirectAttributes,
                    String.format("Đã xóa kỹ năng [%s] khỏi danh mục.", skillInfo.getName()));
        } else {
            FlashMessageHelper.addWarning(redirectAttributes,
                    String.format("Kỹ năng [%s] đã có sinh viên sử dụng nên không được xóa — hệ thống đã tự động chuyển kỹ năng sang trạng thái ẨN!",
                            skillInfo.getName()));
        }
        return "redirect:/admin/skills";
    }

    private void populateSkillListModel(
            String keyword,
            String category,
            String status,
            Authentication authentication,
            Model model) {

        List<Skill> skills = adminSkillService.listSkills(keyword, category, status);
        Map<Long, Long> usageCounts = adminSkillService.getSkillUsageCounts(skills);

        model.addAttribute("skills", skills);
        model.addAttribute("usageCounts", usageCounts);
        model.addAttribute("categories", AdminSkillService.SKILL_CATEGORIES);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("currentAdminEmail", authentication != null ? authentication.getName() : "admin@talentlink.demo");
    }
}
