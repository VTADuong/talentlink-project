package com.talentlink.talentlink.service;

import com.talentlink.talentlink.dto.CreatePartnerDto;
import com.talentlink.talentlink.entity.Company;
import com.talentlink.talentlink.entity.User;
import com.talentlink.talentlink.exception.BusinessException;
import com.talentlink.talentlink.exception.ResourceNotFoundException;
import com.talentlink.talentlink.repository.CompanyRepository;
import com.talentlink.talentlink.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dịch vụ Quản trị tài khoản cho Admin (Task T4E7AA2):
 * - Tìm kiếm theo tên/email, lọc theo vai trò và trạng thái khóa, phân trang.
 * - Tạo tài khoản Đối tác (PARTNER) kèm thông tin Công ty (Company).
 * - Khóa / Mở khóa tài khoản; chặn Admin tự khóa tài khoản của chính mình.
 */
@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserService(
            UserRepository userRepository,
            CompanyRepository companyRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Page<User> searchUsers(String keyword, String roleFilter, String statusFilter, int page, int size) {
        String cleanKeyword = (keyword != null) ? keyword.trim() : "";

        List<String> roles = resolveRoles(roleFilter);
        boolean filterByRole = !roles.isEmpty();

        Boolean locked = null;
        if ("LOCKED".equalsIgnoreCase(statusFilter)) {
            locked = Boolean.TRUE;
        } else if ("ACTIVE".equalsIgnoreCase(statusFilter)) {
            locked = Boolean.FALSE;
        }

        int safePage = Math.max(0, page);
        int safeSize = (size <= 0 || size > 50) ? 8 : size;
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.ASC, "id"));

        return userRepository.searchUsers(
                cleanKeyword,
                filterByRole,
                filterByRole ? roles : List.of("__NONE__"),
                locked,
                pageable);
    }

    private List<String> resolveRoles(String roleFilter) {
        if (roleFilter == null || roleFilter.isBlank() || "ALL".equalsIgnoreCase(roleFilter)) {
            return Collections.emptyList();
        }
        return switch (roleFilter.trim().toUpperCase()) {
            case "ADMIN" -> List.of("ADMIN");
            case "PARTNER", "RECRUITER" -> List.of("PARTNER", "RECRUITER");
            case "STUDENT", "CANDIDATE" -> List.of("STUDENT", "CANDIDATE");
            default -> List.of(roleFilter.trim().toUpperCase());
        };
    }

    @Transactional(readOnly = true)
    public Map<Long, String> getCompanyNameMap(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return companyRepository.findByUserIdIn(userIds).stream()
                .collect(Collectors.toMap(
                        Company::getUserId,
                        Company::getCompanyName,
                        (existing, replacement) -> existing));
    }

    /**
     * Admin tạo tài khoản Đối tác mới kèm bản ghi Công ty.
     */
    @Transactional
    public User createPartnerAccount(CreatePartnerDto dto) {
        LocalDateTime now = LocalDateTime.now();

        User partner = new User();
        partner.setFullName(dto.getFullName().trim());
        partner.setEmail(dto.getEmail().trim().toLowerCase());
        partner.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        partner.setPhone(dto.getPhone() != null && !dto.getPhone().isBlank() ? dto.getPhone().trim() : null);
        partner.setRole("PARTNER");
        partner.setLocked(false);
        partner.setCreatedAt(now);
        partner.setUpdatedAt(now);

        User savedPartner = userRepository.save(partner);

        Company company = new Company();
        company.setUserId(savedPartner.getId());
        company.setCompanyName(dto.getCompanyName().trim());
        company.setWebsite(dto.getCompanyWebsite() != null && !dto.getCompanyWebsite().isBlank()
                ? dto.getCompanyWebsite().trim() : null);
        company.setAddress(dto.getCompanyAddress() != null && !dto.getCompanyAddress().isBlank()
                ? dto.getCompanyAddress().trim() : null);
        company.setCreatedAt(now);
        company.setUpdatedAt(now);

        companyRepository.save(company);
        return savedPartner;
    }

    /**
     * Khóa tài khoản người dùng.
     * Chặn tuyệt đối không cho Admin tự khóa chính tài khoản của mình.
     */
    @Transactional
    public User lockUser(Long targetUserId, String lockedReason, String currentAdminEmail) {
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản", targetUserId));

        if (currentAdminEmail != null && targetUser.getEmail().equalsIgnoreCase(currentAdminEmail.trim())) {
            throw new BusinessException(
                    "Bạn không thể tự khóa tài khoản Quản trị viên của chính mình!",
                    "/admin/users");
        }

        String finalReason = (lockedReason != null && !lockedReason.isBlank())
                ? lockedReason.trim()
                : "Tài khoản bị khóa bởi Quản trị viên hệ thống.";

        targetUser.setLocked(true);
        targetUser.setLockedReason(finalReason);
        targetUser.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(targetUser);
    }

    /**
     * Mở khóa tài khoản người dùng.
     */
    @Transactional
    public User unlockUser(Long targetUserId) {
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản", targetUserId));

        targetUser.setLocked(false);
        targetUser.setLockedReason(null);
        targetUser.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(targetUser);
    }
}
