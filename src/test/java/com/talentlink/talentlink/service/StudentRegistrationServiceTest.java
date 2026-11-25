package com.talentlink.talentlink.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.talentlink.talentlink.dto.StudentRegisterDto;
import com.talentlink.talentlink.dto.StudentRegistrationValidator;
import com.talentlink.talentlink.entity.Profile;
import com.talentlink.talentlink.entity.User;
import com.talentlink.talentlink.repository.ProfileRepository;
import com.talentlink.talentlink.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

/**
 * Unit Test (JUnit 5 + Mockito) cho chức năng Đăng ký sinh viên (Task T4E7AA0 & T4E7AAA).
 */
@ExtendWith(MockitoExtension.class)
class StudentRegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private StudentRegistrationService registrationService;

    private StudentRegistrationValidator registrationValidator;

    @BeforeEach
    void setUp() {
        registrationValidator = new StudentRegistrationValidator(userRepository);
    }

    @Test
    @DisplayName("1. Đăng ký sinh viên hợp lệ: Mã hóa mật khẩu BCrypt và tự tạo hồ sơ trống ở trạng thái Riêng tư (PRIVATE)")
    void registerStudent_ValidInput_EncodesBCryptPasswordAndCreatesPrivateEmptyProfile() {
        StudentRegisterDto dto = new StudentRegisterDto();
        dto.setFullName("  Vũ Thị Ánh Dương  ");
        dto.setEmail("  Duong.Student@TalentLink.Demo  ");
        dto.setPassword("Secret123@");
        dto.setConfirmPassword("Secret123@");

        when(passwordEncoder.encode("Secret123@")).thenReturn("$2a$10$hashedBCryptPasswordValue");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(101L);
            return u;
        });

        User savedUser = registrationService.registerStudent(dto);

        assertNotNull(savedUser);
        assertEquals(101L, savedUser.getId());
        assertEquals("Vũ Thị Ánh Dương", savedUser.getFullName());
        assertEquals("duong.student@talentlink.demo", savedUser.getEmail());
        assertEquals("$2a$10$hashedBCryptPasswordValue", savedUser.getPasswordHash());
        assertEquals("STUDENT", savedUser.getRole());
        assertFalse(savedUser.isLocked());

        ArgumentCaptor<Profile> profileCaptor = ArgumentCaptor.forClass(Profile.class);
        verify(profileRepository).save(profileCaptor.capture());
        Profile createdProfile = profileCaptor.getValue();

        assertEquals(101L, createdProfile.getUserId());
        assertEquals("PRIVATE", createdProfile.getVisibility());
        assertFalse(createdProfile.isOpenToWork());
        assertEquals("DRAFT", createdProfile.getApprovalStatus());
    }

    @Test
    @DisplayName("2. Validator đăng ký (Trường hợp lỗi): Email đã tồn tại trong hệ thống bị từ chối")
    void validateRegistration_DuplicateEmail_AddsEmailFieldError() {
        StudentRegisterDto dto = new StudentRegisterDto();
        dto.setFullName("Nguyễn Văn A");
        dto.setEmail("student01@talentlink.demo");
        dto.setPassword("Password123@");
        dto.setConfirmPassword("Password123@");

        User existing = new User();
        existing.setId(1L);
        existing.setEmail("student01@talentlink.demo");
        when(userRepository.findByEmailIgnoreCase("student01@talentlink.demo"))
                .thenReturn(Optional.of(existing));

        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "registerDto");
        registrationValidator.validate(dto, bindingResult);

        assertTrue(bindingResult.hasFieldErrors("email"));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("3. Validator đăng ký (Trường hợp lỗi): Mật khẩu nhập lại không khớp bị từ chối")
    void validateRegistration_PasswordMismatch_AddsConfirmPasswordFieldError() {
        StudentRegisterDto dto = new StudentRegisterDto();
        dto.setFullName("Nguyễn Văn B");
        dto.setEmail("newstudent@talentlink.demo");
        dto.setPassword("Password123@");
        dto.setConfirmPassword("Different999@");

        when(userRepository.findByEmailIgnoreCase("newstudent@talentlink.demo"))
                .thenReturn(Optional.empty());

        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "registerDto");
        registrationValidator.validate(dto, bindingResult);

        assertTrue(bindingResult.hasFieldErrors("confirmPassword"));
        assertFalse(bindingResult.hasFieldErrors("email"));
    }
}
