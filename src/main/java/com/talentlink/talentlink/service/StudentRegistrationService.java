package com.talentlink.talentlink.service;

import com.talentlink.talentlink.dto.StudentRegisterDto;
import com.talentlink.talentlink.entity.Profile;
import com.talentlink.talentlink.entity.User;
import com.talentlink.talentlink.repository.ProfileRepository;
import com.talentlink.talentlink.repository.UserRepository;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dịch vụ đăng ký tài khoản sinh viên (Task T4E7AA0):
 * - Mã hóa mật khẩu bằng BCrypt.
 * - Lưu tài khoản sinh viên (role = STUDENT).
 * - Tự động khởi tạo hồ sơ trống (Profile) ở trạng thái Riêng tư (visibility = PRIVATE, openToWork = false).
 */
@Service
public class StudentRegistrationService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentRegistrationService(
            UserRepository userRepository,
            ProfileRepository profileRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerStudent(StudentRegisterDto dto) {
        LocalDateTime now = LocalDateTime.now();

        // 1. Tạo tài khoản sinh viên với mật khẩu mã hóa BCrypt
        User student = new User();
        student.setFullName(dto.getFullName().trim());
        student.setEmail(dto.getEmail().trim().toLowerCase());
        student.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        student.setRole("STUDENT");
        student.setLocked(false);
        student.setCreatedAt(now);
        student.setUpdatedAt(now);

        User savedUser = userRepository.save(student);

        // 2. Tự động tạo hồ sơ trống ở trạng thái Riêng tư (PRIVATE)
        Profile emptyProfile = new Profile();
        emptyProfile.setUserId(savedUser.getId());
        emptyProfile.setHeadline("");
        emptyProfile.setBio("");
        emptyProfile.setYearsOfExperience(0);
        emptyProfile.setOpenToWork(false);
        emptyProfile.setVisibility("PRIVATE");
        emptyProfile.setApprovalStatus("DRAFT");
        emptyProfile.setCreatedAt(now);
        emptyProfile.setUpdatedAt(now);

        profileRepository.save(emptyProfile);

        return savedUser;
    }
}
