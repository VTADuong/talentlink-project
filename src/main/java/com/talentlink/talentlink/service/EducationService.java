package com.talentlink.talentlink.service;

import com.talentlink.talentlink.dto.EducationDto;
import com.talentlink.talentlink.entity.Education;
import com.talentlink.talentlink.entity.Profile;
import com.talentlink.talentlink.exception.ForbiddenOperationException;
import com.talentlink.talentlink.exception.ResourceNotFoundException;
import com.talentlink.talentlink.repository.EducationRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dịch vụ Quản lý Học vấn của sinh viên (Task T4E7AA4):
 * - Danh sách học vấn trên trang hồ sơ.
 * - Thêm / Sửa / Xóa học vấn.
 * - Sửa hoặc xóa học vấn của người khác (đổi id trên URL) bị chặn 403 Forbidden.
 */
@Service
public class EducationService {

    private final EducationRepository educationRepository;
    private final MyProfileService myProfileService;

    public EducationService(EducationRepository educationRepository, MyProfileService myProfileService) {
        this.educationRepository = educationRepository;
        this.myProfileService = myProfileService;
    }

    @Transactional
    public List<Education> listMyEducations(String currentEmail) {
        Profile myProfile = myProfileService.getOrCreateMyProfile(currentEmail);
        return educationRepository.findByProfileIdOrderByStartDateDescIdDesc(myProfile.getId());
    }

    /**
     * Lấy thông tin học vấn để đưa lên form sửa.
     * Nếu educationId thuộc về hồ sơ của người khác -> Chặn 403 Forbidden.
     */
    @Transactional
    public EducationDto getMyEducationDtoForEdit(Long educationId, String currentEmail) {
        Education education = getOwnedEducationOrThrow403(educationId, currentEmail);

        EducationDto dto = new EducationDto();
        dto.setId(education.getId());
        dto.setSchoolName(education.getSchoolName());
        dto.setMajor(education.getMajor());
        dto.setDegree(education.getDegree());
        dto.setStartDate(education.getStartDate());
        dto.setEndDate(education.getEndDate());
        dto.setGpa(education.getGpa());
        dto.setDescription(education.getDescription());
        return dto;
    }

    @Transactional
    public Education createEducation(String currentEmail, EducationDto dto) {
        Profile myProfile = myProfileService.getOrCreateMyProfile(currentEmail);

        Education education = new Education();
        education.setProfileId(myProfile.getId());
        applyDtoToEntity(dto, education);
        education.setCreatedAt(LocalDateTime.now());
        return educationRepository.save(education);
    }

    /**
     * Cập nhật học vấn theo id trên URL.
     * Nếu đổi id trên URL sang học vấn của người khác -> Chặn 403 Forbidden.
     */
    @Transactional
    public Education updateEducation(Long educationId, String currentEmail, EducationDto dto) {
        Education education = getOwnedEducationOrThrow403(educationId, currentEmail);
        applyDtoToEntity(dto, education);
        return educationRepository.save(education);
    }

    /**
     * Xóa học vấn theo id trên URL.
     * Nếu đổi id trên URL sang học vấn của người khác -> Chặn 403 Forbidden.
     */
    @Transactional
    public void deleteEducation(Long educationId, String currentEmail) {
        Education education = getOwnedEducationOrThrow403(educationId, currentEmail);
        educationRepository.delete(education);
    }

    private Education getOwnedEducationOrThrow403(Long educationId, String currentEmail) {
        Profile myProfile = myProfileService.getOrCreateMyProfile(currentEmail);
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new ResourceNotFoundException("Học vấn", educationId));

        if (!education.getProfileId().equals(myProfile.getId())) {
            throw new ForbiddenOperationException(
                    "Bạn không có quyền chỉnh sửa hoặc xóa thông tin học vấn của người khác (Mã học vấn #" + educationId + ")!");
        }
        return education;
    }

    private void applyDtoToEntity(EducationDto dto, Education education) {
        education.setSchoolName(dto.getSchoolName().trim());
        education.setMajor(dto.getMajor().trim());
        education.setDegree(dto.getDegree() != null && !dto.getDegree().isBlank() ? dto.getDegree().trim() : null);
        education.setStartDate(dto.getStartDate());
        education.setEndDate(dto.getEndDate());
        education.setGpa(dto.getGpa());
        education.setDescription(dto.getDescription() != null && !dto.getDescription().isBlank()
                ? dto.getDescription().trim() : null);
    }
}
