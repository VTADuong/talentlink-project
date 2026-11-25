package com.talentlink.talentlink.config;

import com.talentlink.talentlink.entity.User;
import com.talentlink.talentlink.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

/**
 * Xử lý khi đăng nhập thất bại:
 * - Nếu tài khoản bị khóa (LockedException), truy vấn và hiển thị rõ lý do khóa (locked_reason).
 * - Nếu sai email hoặc mật khẩu, thông báo lỗi rõ ràng trên form đăng nhập.
 */
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    public static final String AUTH_ERROR_SESSION_KEY = "AUTH_ERROR_MESSAGE";
    public static final String LAST_EMAIL_SESSION_KEY = "LAST_LOGIN_EMAIL";

    private final UserRepository userRepository;

    public CustomAuthenticationFailureHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        String email = request.getParameter("email");
        String errorMessage = "Email hoặc mật khẩu không chính xác. Vui lòng kiểm tra lại.";
        String redirectQuery = "?error";

        if (isLockedException(exception) || isUserLockedInDatabase(email)) {
            String reason = resolveLockedReason(email);
            errorMessage = "Tài khoản của bạn đã bị khóa và không thể đăng nhập. Lý do: " + reason;
            redirectQuery = "?locked";
        }

        request.getSession().setAttribute(AUTH_ERROR_SESSION_KEY, errorMessage);
        if (email != null) {
            request.getSession().setAttribute(LAST_EMAIL_SESSION_KEY, email.trim());
        }

        response.sendRedirect(request.getContextPath() + "/login" + redirectQuery);
    }

    private boolean isLockedException(Throwable ex) {
        Throwable current = ex;
        while (current != null) {
            if (current instanceof LockedException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private boolean isUserLockedInDatabase(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return userRepository.findByEmailIgnoreCase(email.trim())
                .map(User::isLocked)
                .orElse(false);
    }

    private String resolveLockedReason(String email) {
        if (email == null || email.isBlank()) {
            return "Vi phạm quy định sử dụng hệ thống. Vui lòng liên hệ Quản trị viên.";
        }
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email.trim());
        if (userOpt.isPresent()) {
            String reason = userOpt.get().getLockedReason();
            if (reason != null && !reason.isBlank()) {
                return reason;
            }
        }
        return "Tài khoản đang tạm khóa bởi Quản trị viên hệ thống.";
    }
}
