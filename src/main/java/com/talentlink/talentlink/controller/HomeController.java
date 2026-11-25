package com.talentlink.talentlink.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/flash/success")
    public String triggerSuccessFlash(RedirectAttributes redirectAttributes) {
        FlashMessageHelper.addSuccess(redirectAttributes,
                "Thao tác đã được thực hiện thành công! Thông báo flash hiển thị trên layout chung.");
        return "redirect:/";
    }

    @PostMapping("/flash/error")
    public String triggerErrorFlash(RedirectAttributes redirectAttributes) {
        FlashMessageHelper.addError(redirectAttributes,
                "Không thể hoàn tất thao tác do dữ liệu chưa hợp lệ. Vui lòng kiểm tra lại!");
        return "redirect:/";
    }
}
