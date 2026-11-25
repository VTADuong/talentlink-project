package com.talentlink.talentlink.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * Sau khi đăng nhập thành công, tự động chuyển hướng người dùng
 * về đúng trang chủ theo vai trò:
 * - ADMIN   -> /admin/dashboard
 * - PARTNER -> /partner/dashboard
 * - STUDENT -> /me/dashboard
 */
@Component
public class RoleBasedAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(CustomAuthenticationFailureHandler.AUTH_ERROR_SESSION_KEY);
            session.removeAttribute(CustomAuthenticationFailureHandler.LAST_EMAIL_SESSION_KEY);
        }

        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        String targetUrl = "/";
        if (roles.contains("ROLE_ADMIN")) {
            targetUrl = "/admin/dashboard";
        } else if (roles.contains("ROLE_PARTNER")) {
            targetUrl = "/partner/dashboard";
        } else if (roles.contains("ROLE_STUDENT")) {
            targetUrl = "/me/dashboard";
        }

        response.sendRedirect(request.getContextPath() + targetUrl);
    }
}
