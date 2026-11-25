package com.talentlink.talentlink.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.talentlink.talentlink.dto.EducationDto;
import com.talentlink.talentlink.entity.Education;
import com.talentlink.talentlink.entity.Profile;
import com.talentlink.talentlink.exception.ForbiddenOperationException;
import com.talentlink.talentlink.exception.ResourceNotFoundException;
import com.talentlink.talentlink.repository.EducationRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit Test (JUnit 5 + Mockito) cho chức năng Quản lý Học vấn (Task T4E7AA4 & T4E7AAA).
 */
@ExtendWith(MockitoExtension.class)
class EducationServiceTest {

    @Mock
    private EducationRepository educationRepository;

    @Mock
    private MyProfileService myProfileService;

    @InjectMocks
    private EducationService educationService;

    @Test
    @DisplayName("9. Thêm học vấn hợp lệ: Lưu trường, chuyên ngành, ngày bắt đầu - kết thúc và GPA (0–4)")
    void createEducation_ValidDto_SavesEducationWithCurrentStudentProfileId() {
        Profile myProfile = new Profile();
        myProfile.setId(50L);
        myProfile.setUserId(1L);

        EducationDto dto = new EducationDto();
        dto.setSchoolName("  Đại học Bách Khoa Hà Nội  ");
        dto.setMajor("  Kỹ thuật Phần mềm  ");
        dto.setDegree("Kỹ sư");
        dto.setStartDate(LocalDate.of(2022, 9, 1));
        dto.setEndDate(LocalDate.of(2026, 6, 30));
        dto.setGpa(new BigDecimal("3.65"));
        dto.setDescription("Sinh viên giỏi");

        when(myProfileService.getOrCreateMyProfile("student01@talentlink.demo")).thenReturn(myProfile);
        when(educationRepository.save(any(Education.class))).thenAnswer(inv -> {
            Education e = inv.getArgument(0);
            e.setId(200L);
            return e;
        });

        Education saved = educationService.createEducation("student01@talentlink.demo", dto);

        assertNotNull(saved);
        assertEquals(200L, saved.getId());
        assertEquals(50L, saved.getProfileId());
        assertEquals("Đại học Bách Khoa Hà Nội", saved.getSchoolName());
        assertEquals("Kỹ thuật Phần mềm", saved.getMajor());
        assertEquals(new BigDecimal("3.65"), saved.getGpa());
    }

    @Test
    @DisplayName("10. Sửa học vấn của người khác (Trường hợp lỗi 403): Đổi ID trên URL bị chặn ForbiddenOperationException")
    void updateEducation_BelongsToAnotherStudentProfile_ThrowsForbiddenOperationException() {
        Profile student1Profile = new Profile();
        student1Profile.setId(50L);

        Education student2Education = new Education();
        student2Education.setId(999L);
        student2Education.setProfileId(88L); // Thuộc hồ sơ của sinh viên khác (#88)

        EducationDto dto = new EducationDto();
        dto.setSchoolName("Trường Sửa Trái Phép");
        dto.setMajor("CNTT");
        dto.setStartDate(LocalDate.of(2021, 9, 1));
        dto.setEndDate(LocalDate.of(2025, 6, 1));

        when(myProfileService.getOrCreateMyProfile("student01@talentlink.demo")).thenReturn(student1Profile);
        when(educationRepository.findById(999L)).thenReturn(Optional.of(student2Education));

        assertThrows(ForbiddenOperationException.class,
                () -> educationService.updateEducation(999L, "student01@talentlink.demo", dto));

        verify(educationRepository, never()).save(any());
    }

    @Test
    @DisplayName("11. Xóa học vấn của người khác (Trường hợp lỗi 403): Đổi ID trên URL bị chặn ForbiddenOperationException")
    void deleteEducation_BelongsToAnotherStudentProfile_ThrowsForbiddenOperationException() {
        Profile student1Profile = new Profile();
        student1Profile.setId(50L);

        Education student2Education = new Education();
        student2Education.setId(777L);
        student2Education.setProfileId(99L); // Hồ sơ người khác

        when(myProfileService.getOrCreateMyProfile("student01@talentlink.demo")).thenReturn(student1Profile);
        when(educationRepository.findById(777L)).thenReturn(Optional.of(student2Education));

        assertThrows(ForbiddenOperationException.class,
                () -> educationService.deleteEducation(777L, "student01@talentlink.demo"));

        verify(educationRepository, never()).delete(any());
    }

    @Test
    @DisplayName("12. Truy cập học vấn không tồn tại (Trường hợp lỗi 404): Ném ResourceNotFoundException")
    void getMyEducationDtoForEdit_NonExistentId_ThrowsResourceNotFoundException() {
        Profile student1Profile = new Profile();
        student1Profile.setId(50L);

        when(myProfileService.getOrCreateMyProfile("student01@talentlink.demo")).thenReturn(student1Profile);
        when(educationRepository.findById(4040L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> educationService.getMyEducationDtoForEdit(4040L, "student01@talentlink.demo"));
    }
}
