package com.talentlink.talentlink.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller hiển thị các trang lỗi thân thiện (403, 404, 500).
 */
@Controller
public class ErrorPageController {

    @GetMapping("/error/403")
    public String accessDenied(HttpServletResponse response, Model model) {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        model.addAttribute("errorCode", 403);
        model.addAttribute("errorTitle", "Truy cập bị từ chối");
        model.addAttribute("userFriendlyMessage",
                "Tài khoản của bạn không có quyền thực hiện thao tác hoặc truy cập trang này.");
        return "error/403";
    }
}
