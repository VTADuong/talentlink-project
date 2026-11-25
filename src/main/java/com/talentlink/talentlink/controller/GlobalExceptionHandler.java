package com.talentlink.talentlink.controller;

import com.talentlink.talentlink.exception.BusinessException;
import com.talentlink.talentlink.exception.ForbiddenOperationException;
import com.talentlink.talentlink.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Xử lý lỗi toàn cục (@ControllerAdvice) cho toàn bộ ứng dụng MVC.
 * Bắt lỗi nghiệp vụ, không tìm thấy (404), không có quyền (403) và lỗi hệ thống (500).
 * Đảm bảo trang lỗi thân thiện và tuyệt đối không lộ stack trace ra giao diện.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(
            BusinessException ex,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes,
            Model model) {

        log.warn("Business rule violation at [{}]: {}", request.getRequestURI(), ex.getMessage());

        if (ex.getRedirectUrl() != null && !ex.getRedirectUrl().isBlank()) {
            FlashMessageHelper.addError(redirectAttributes, ex.getMessage());
            return "redirect:" + ex.getRedirectUrl();
        }

        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank() && !referer.contains("/error")) {
            FlashMessageHelper.addError(redirectAttributes, ex.getMessage());
            return "redirect:" + referer;
        }

        response.setStatus(HttpStatus.BAD_REQUEST.value());
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorCode", 400);
        model.addAttribute("errorTitle", "Yêu cầu không hợp lệ");
        model.addAttribute("requestUri", request.getRequestURI());
        return "error/error";
    }

    @ExceptionHandler({ResourceNotFoundException.class, NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(Exception ex, HttpServletRequest request, Model model) {
        log.warn("Resource not found at [{}]: {}", request.getRequestURI(), ex.getMessage());

        String friendlyMessage = (ex instanceof ResourceNotFoundException)
                ? ex.getMessage()
                : "Trang hoặc đường dẫn bạn yêu cầu không tồn tại hoặc đã bị di chuyển.";

        model.addAttribute("errorCode", 404);
        model.addAttribute("errorTitle", "Không tìm thấy trang hoặc dữ liệu");
        model.addAttribute("userFriendlyMessage", friendlyMessage);
        model.addAttribute("requestUri", request.getRequestURI());
        return "error/404";
    }

    @ExceptionHandler({AccessDeniedException.class, ForbiddenOperationException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDeniedException(Exception ex, HttpServletRequest request, Model model) {
        log.warn("Access denied at [{}]: {}", request.getRequestURI(), ex.getMessage());

        String friendlyMessage = (ex instanceof ForbiddenOperationException)
                ? ex.getMessage()
                : "Bạn không có quyền truy cập vào chức năng hoặc tài nguyên này.";

        model.addAttribute("errorCode", 403);
        model.addAttribute("errorTitle", "Truy cập bị từ chối");
        model.addAttribute("userFriendlyMessage", friendlyMessage);
        model.addAttribute("requestUri", request.getRequestURI());
        return "error/403";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, HttpServletRequest request, Model model) {
        log.error("Unexpected system error at [{}]", request.getRequestURI(), ex);

        model.addAttribute("errorCode", 500);
        model.addAttribute("errorTitle", "Đã xảy ra lỗi hệ thống");
        model.addAttribute("userFriendlyMessage",
                "Hệ thống đang gặp sự cố tạm thời khi xử lý yêu cầu của bạn. Vui lòng thử lại sau.");
        model.addAttribute("requestUri", request.getRequestURI());
        return "error/500";
    }
}
