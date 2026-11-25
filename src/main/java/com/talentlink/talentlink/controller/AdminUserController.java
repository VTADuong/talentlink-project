package com.talentlink.talentlink.controller;

import com.talentlink.talentlink.dto.CreatePartnerDto;
import com.talentlink.talentlink.entity.User;
import com.talentlink.talentlink.service.AdminUserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
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
 * Controller Quản trị tài khoản tại /admin/users (Task T4E7AA2):
 * - Bảng tài khoản phân trang, tìm theo tên/email, lọc theo vai trò & trạng thái.
 * - Admin tạo tài khoản Đối tác (kèm tên công ty).
 * - Khóa / Mở khóa tài khoản có hỏi xác nhận; Admin không tự khóa chính mình.
 */
@Controller
@RequestMapping("/admin")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping("/accounts")
    public String redirectAccountsToUsers() {
        return "redirect:/admin/users";
    }

    @GetMapping("/users")
    public String listUsers(
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "role", defaultValue = "ALL") String role,
            @RequestParam(value = "status", defaultValue = "ALL") String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "8") int size,
            Authentication authentication,
            Model model) {

        populateUserPageModel(keyword, role, status, page, size, authentication, model);
        if (!model.containsAttribute("createPartnerDto")) {
            model.addAttribute("createPartnerDto", new CreatePartnerDto());
        }
        return "admin/users";
    }

    @PostMapping("/users/partners")
    public String createPartner(
            @Valid @ModelAttribute("createPartnerDto") CreatePartnerDto createPartnerDto,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            populateUserPageModel("", "ALL", "ALL", 0, 8, authentication, model);
            model.addAttribute("showPartnerForm", true);
            return "admin/users";
        }

        User created = adminUserService.createPartnerAccount(createPartnerDto);
        FlashMessageHelper.addSuccess(redirectAttributes,
                String.format("Đã tạo tài khoản Đối tác [%s] kèm công ty [%s] thành công!",
                        created.getEmail(), createPartnerDto.getCompanyName().trim()));
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/lock")
    public String lockUser(
            @PathVariable("id") Long id,
            @RequestParam(value = "lockedReason", required = false) String lockedReason,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        String currentAdminEmail = authentication != null ? authentication.getName() : "";
        User lockedUser = adminUserService.lockUser(id, lockedReason, currentAdminEmail);
        FlashMessageHelper.addWarning(redirectAttributes,
                String.format("Đã khóa tài khoản [%s]. Lý do: %s",
                        lockedUser.getEmail(), lockedUser.getLockedReason()));
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/unlock")
    public String unlockUser(
            @PathVariable("id") Long id,
            RedirectAttributes redirectAttributes) {

        User unlockedUser = adminUserService.unlockUser(id);
        FlashMessageHelper.addSuccess(redirectAttributes,
                String.format("Đã mở khóa tài khoản [%s] thành công!", unlockedUser.getEmail()));
        return "redirect:/admin/users";
    }

    private void populateUserPageModel(
            String keyword,
            String role,
            String status,
            int page,
            int size,
            Authentication authentication,
            Model model) {

        Page<User> userPage = adminUserService.searchUsers(keyword, role, status, page, size);
        List<Long> userIds = userPage.getContent().stream().map(User::getId).toList();
        Map<Long, String> companyNames = adminUserService.getCompanyNameMap(userIds);

        model.addAttribute("userPage", userPage);
        model.addAttribute("companyNames", companyNames);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedRole", role);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("currentAdminEmail", authentication != null ? authentication.getName() : "");
    }
}
