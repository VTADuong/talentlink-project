package com.talentlink.talentlink.service;

import com.talentlink.talentlink.entity.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Thông tin người dùng đăng nhập trong Spring Security Session.
 * Hỗ trợ ánh xạ vai trò ADMIN, PARTNER (RECRUITER), STUDENT (CANDIDATE)
 * và kiểm tra trạng thái khóa tài khoản (is_locked + locked_reason).
 */
public class CustomUserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String passwordHash;
    private final String fullName;
    private final String role;
    private final boolean locked;
    private final String lockedReason;
    private final List<GrantedAuthority> authorities;

    public CustomUserPrincipal(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
        this.fullName = user.getFullName();
        this.role = user.getRole();
        this.locked = user.isLocked();
        this.lockedReason = user.getLockedReason();
        this.authorities = buildAuthorities(user.getRole());
    }

    private static List<GrantedAuthority> buildAuthorities(String rawRole) {
        List<GrantedAuthority> auths = new ArrayList<>();
        if (rawRole == null) {
            return auths;
        }
        String normalized = rawRole.trim().toUpperCase();
        switch (normalized) {
            case "ADMIN" -> auths.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            case "PARTNER", "RECRUITER" -> {
                auths.add(new SimpleGrantedAuthority("ROLE_PARTNER"));
                auths.add(new SimpleGrantedAuthority("ROLE_RECRUITER"));
            }
            case "STUDENT", "CANDIDATE" -> {
                auths.add(new SimpleGrantedAuthority("ROLE_STUDENT"));
                auths.add(new SimpleGrantedAuthority("ROLE_CANDIDATE"));
            }
            default -> auths.add(new SimpleGrantedAuthority("ROLE_" + normalized));
        }
        return auths;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }

    public String getLockedReason() {
        return lockedReason;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
