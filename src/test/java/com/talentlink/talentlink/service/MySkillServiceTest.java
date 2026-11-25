package com.talentlink.talentlink.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.talentlink.talentlink.dto.AddProfileSkillDto;
import com.talentlink.talentlink.dto.StudentSkillViewDto;
import com.talentlink.talentlink.entity.Profile;
import com.talentlink.talentlink.entity.ProfileSkill;
import com.talentlink.talentlink.entity.Skill;
import com.talentlink.talentlink.exception.BusinessException;
import com.talentlink.talentlink.exception.ForbiddenOperationException;
import com.talentlink.talentlink.repository.ProfileSkillRepository;
import com.talentlink.talentlink.repository.SkillRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit Test (JUnit 5 + Mockito) cho chức năng Kỹ năng của tôi (Task T4E7AA6 & T4E7AAA).
 */
@ExtendWith(MockitoExtension.class)
class MySkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private ProfileSkillRepository profileSkillRepository;

    @Mock
    private MyProfileService myProfileService;

    @InjectMocks
    private MySkillService mySkillService;

    @Test
    @DisplayName("13. Thêm kỹ năng hợp lệ vào hồ sơ với mức tự chấm 1–5")
    void addSkillToMyProfile_ValidSkillAndProficiency_SavesProfileSkill() {
        Profile myProfile = new Profile();
        myProfile.setId(10L);

        Skill javaSkill = new Skill();
        javaSkill.setId(1L);
        javaSkill.setName("Java");
        javaSkill.setCategory("Ngôn ngữ");

        AddProfileSkillDto dto = new AddProfileSkillDto();
        dto.setSkillId(1L);
        dto.setProficiencyLevel(4);

        when(myProfileService.getOrCreateMyProfile("student01@talentlink.demo")).thenReturn(myProfile);
        when(skillRepository.findById(1L)).thenReturn(Optional.of(javaSkill));
        when(profileSkillRepository.existsByProfileIdAndSkillId(10L, 1L)).thenReturn(false);

        Skill added = mySkillService.addSkillToMyProfile("student01@talentlink.demo", dto);

        assertNotNull(added);
        assertEquals("Java", added.getName());

        ArgumentCaptor<ProfileSkill> psCaptor = ArgumentCaptor.forClass(ProfileSkill.class);
        verify(profileSkillRepository).save(psCaptor.capture());
        assertEquals(10L, psCaptor.getValue().getProfileId());
        assertEquals(1L, psCaptor.getValue().getSkillId());
        assertEquals(4, psCaptor.getValue().getProficiencyLevel());
    }

    @Test
    @DisplayName("14. Không cho phép chọn trùng kỹ năng đã có trong hồ sơ (Trường hợp lỗi)")
    void addSkillToMyProfile_DuplicateSkill_ThrowsBusinessException() {
        Profile myProfile = new Profile();
        myProfile.setId(10L);

        Skill javaSkill = new Skill();
        javaSkill.setId(1L);
        javaSkill.setName("Java");
        javaSkill.setCategory("Ngôn ngữ");

        AddProfileSkillDto dto = new AddProfileSkillDto();
        dto.setSkillId(1L);
        dto.setProficiencyLevel(5);

        when(myProfileService.getOrCreateMyProfile("student01@talentlink.demo")).thenReturn(myProfile);
        when(skillRepository.findById(1L)).thenReturn(Optional.of(javaSkill));
        when(profileSkillRepository.existsByProfileIdAndSkillId(10L, 1L)).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> mySkillService.addSkillToMyProfile("student01@talentlink.demo", dto));

        assertTrue(ex.getMessage().contains("Không được chọn trùng kỹ năng"));
        verify(profileSkillRepository, never()).save(any());
    }

    @Test
    @DisplayName("15. Hiển thị kỹ năng theo từng nhóm chuẩn (Ngôn ngữ, Framework) kèm mô tả mức tự chấm")
    void getMySkillsGroupedByCategory_ReturnsSkillsGroupedAndSortedByProficiency() {
        Profile myProfile = new Profile();
        myProfile.setId(10L);

        ProfileSkill psJava = new ProfileSkill();
        psJava.setId(100L);
        psJava.setProfileId(10L);
        psJava.setSkillId(1L);
        psJava.setProficiencyLevel(5);

        ProfileSkill psSpring = new ProfileSkill();
        psSpring.setId(101L);
        psSpring.setProfileId(10L);
        psSpring.setSkillId(2L);
        psSpring.setProficiencyLevel(4);

        Skill javaSkill = new Skill();
        javaSkill.setId(1L);
        javaSkill.setName("Java");
        javaSkill.setCategory("Ngôn ngữ");

        Skill springSkill = new Skill();
        springSkill.setId(2L);
        springSkill.setName("Spring Boot");
        springSkill.setCategory("Framework");

        when(myProfileService.getOrCreateMyProfile("student01@talentlink.demo")).thenReturn(myProfile);
        when(profileSkillRepository.findByProfileId(10L)).thenReturn(List.of(psJava, psSpring));
        when(skillRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(javaSkill, springSkill));

        Map<String, List<StudentSkillViewDto>> grouped =
                mySkillService.getMySkillsGroupedByCategory("student01@talentlink.demo");

        assertEquals(2, grouped.size());
        assertTrue(grouped.containsKey("Ngôn ngữ"));
        assertTrue(grouped.containsKey("Framework"));
        assertEquals("Java", grouped.get("Ngôn ngữ").get(0).getSkillName());
        assertEquals(5, grouped.get("Ngôn ngữ").get(0).getProficiencyLevel());
    }

    @Test
    @DisplayName("16. Bỏ kỹ năng của hồ sơ người khác (Trường hợp lỗi 403): Ném ForbiddenOperationException")
    void removeSkillFromMyProfile_BelongsToAnotherProfile_ThrowsForbiddenOperationException() {
        Profile myProfile = new Profile();
        myProfile.setId(10L);

        ProfileSkill otherProfileSkill = new ProfileSkill();
        otherProfileSkill.setId(555L);
        otherProfileSkill.setProfileId(99L); // Hồ sơ của sinh viên khác

        when(myProfileService.getOrCreateMyProfile("student01@talentlink.demo")).thenReturn(myProfile);
        when(profileSkillRepository.findById(555L)).thenReturn(Optional.of(otherProfileSkill));

        assertThrows(ForbiddenOperationException.class,
                () -> mySkillService.removeSkillFromMyProfile(555L, "student01@talentlink.demo"));

        verify(profileSkillRepository, never()).delete(any());
    }
}
