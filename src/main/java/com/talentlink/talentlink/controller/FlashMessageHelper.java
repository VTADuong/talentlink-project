package com.talentlink.talentlink.controller;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Tiện ích gắn thông báo Flash (thành công / lỗi / cảnh báo / thông tin)
 * qua RedirectAttributes để hiển thị thống nhất ở vùng thông báo của Layout chung.
 */
public final class FlashMessageHelper {

    public static final String SUCCESS_KEY = "successMessage";
    public static final String ERROR_KEY = "errorMessage";
    public static final String WARNING_KEY = "warningMessage";
    public static final String INFO_KEY = "infoMessage";

    private FlashMessageHelper() {
    }

    public static void addSuccess(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute(SUCCESS_KEY, message);
    }

    public static void addError(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute(ERROR_KEY, message);
    }

    public static void addWarning(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute(WARNING_KEY, message);
    }

    public static void addInfo(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute(INFO_KEY, message);
    }
}
