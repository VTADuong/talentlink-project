package com.talentlink.talentlink.service;

import com.talentlink.talentlink.dto.MyProfileUpdateDto;
import com.talentlink.talentlink.entity.Profile;
import com.talentlink.talentlink.entity.User;
import com.talentlink.talentlink.exception.ForbiddenOperationException;
import com.talentlink.talentlink.exception.ResourceNotFoundException;
import com.talentlink.talentlink.repository.ProfileRepository;
import com.talentlink.talentlink.repository.UserRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dịch vụ xử lý Xem và Cập nhật "Hồ sơ của tôi" tại /me/profile (Task T4E7AA3):
 * - Đảm bảo chỉ chủ hồ sơ mới có quyền chỉnh sửa hồ sơ của chính mình.
 * - Cập nhật đồng bộ họ tên, số điện thoại (bảng users) và ngày sinh, giới thiệu bản thân,
 *   vị trí mong muốn, nơi làm việc mong muốn (bảng profiles).
 */
@Service
public class MyProfileService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    public MyProfileService(UserRepository userRepository, ProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    @Transactional
    public User getCurrentUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản người dùng: " + email));
    }

    /**
     * Lấy hồ sơ của sinh viên đang đăng nhập (tự động tạo hồ sơ trống ở trạng thái Riêng tư nếu chưa có).
     */
    @Transactional
    public Profile getOrCreateMyProfile(String currentEmail) {
        User user = getCurrentUser(currentEmail);
        return profileRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    LocalDateTime now = LocalDateTime.now();
                    Profile profile = new Profile();
                    profile.setUserId(user.getId());
                    profile.setHeadline("");
                    profile.setBio("");
                    profile.setLocation("");
                    profile.setYearsOfExperience(0);
                    profile.setOpenToWork(false);
                    profile.setVisibility("PRIVATE");
                    profile.setApprovalStatus("DRAFT");
                    profile.setCreatedAt(now);
                    profile.setUpdatedAt(now);
                    return profileRepository.save(profile);
                });
    }

    @Transactional
    public MyProfileUpdateDto buildFormDto(String currentEmail) {
        User user = getCurrentUser(currentEmail);
        Profile profile = getOrCreateMyProfile(currentEmail);

        MyProfileUpdateDto dto = new MyProfileUpdateDto();
        dto.setProfileId(profile.getId());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setDateOfBirth(profile.getDateOfBirth());
        dto.setDesiredPosition(profile.getHeadline());
        dto.setDesiredLocation(profile.getLocation());
        dto.setBio(profile.getBio());
        return dto;
    }

    /**
     * Cập nhật thông tin hồ sơ của tôi.
     * Kiểm tra chặt chẽ quyền sở hữu: Chỉ chủ hồ sơ mới được phép sửa.
     */
    @Transactional
    public Profile updateMyProfile(String currentEmail, MyProfileUpdateDto dto) {
        User currentUser = getCurrentUser(currentEmail);
        Profile myProfile = getOrCreateMyProfile(currentEmail);

        // Kiểm tra chỉ chủ hồ sơ mới được sửa
        if (dto.getProfileId() != null && !dto.getProfileId().equals(myProfile.getId())) {
            throw new ForbiddenOperationException("Bạn chỉ có quyền chỉnh sửa hồ sơ cá nhân của chính mình!");
        }
        if (!myProfile.getUserId().equals(currentUser.getId())) {
            throw new ForbiddenOperationException("Bạn không phải là chủ sở hữu của hồ sơ này!");
        }

        LocalDateTime now = LocalDateTime.now();

        // Cập nhật họ tên & số điện thoại trên bảng users
        currentUser.setFullName(dto.getFullName().trim());
        currentUser.setPhone(dto.getPhone().trim());
        currentUser.setUpdatedAt(now);
        userRepository.save(currentUser);

        // Cập nhật ngày sinh, vị trí mong muốn, nơi làm việc mong muốn, giới thiệu bản thân trên bảng profiles
        myProfile.setDateOfBirth(dto.getDateOfBirth());
        myProfile.setHeadline(dto.getDesiredPosition().trim());
        myProfile.setLocation(dto.getDesiredLocation().trim());
        myProfile.setBio(dto.getBio().trim());
        myProfile.setUpdatedAt(now);

        return profileRepository.save(myProfile);
    }
}
