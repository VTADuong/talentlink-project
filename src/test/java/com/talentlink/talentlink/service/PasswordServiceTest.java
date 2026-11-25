package com.talentlink.talentlink.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.talentlink.talentlink.dto.ChangePasswordDto;
import com.talentlink.talentlink.dto.ResetPasswordDto;
import com.talentlink.talentlink.entity.PasswordResetOtp;
import com.talentlink.talentlink.entity.User;
import com.talentlink.talentlink.repository.PasswordResetOtpRepository;
import com.talentlink.talentlink.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Unit Test (JUnit 5 + Mockito) cho Đổi mật khẩu và Quên mật khẩu OTP 6 số (Task T4E7AA1 & T4E7AAA).
 */
@ExtendWith(MockitoExtension.class)
class PasswordServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetOtpRepository otpRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordService passwordService;

    @Test
    @DisplayName("4. Đổi mật khẩu thành công khi nhập đúng mật khẩu cũ")
    void changePassword_CorrectCurrentPassword_UpdatesPasswordHashAndReturnsEmptyError() {
        User user = new User();
        user.setId(10L);
        user.setEmail("student01@talentlink.demo");
        user.setPasswordHash("$2a$10$oldPasswordHash");

        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setCurrentPassword("OldPass123@");
        dto.setNewPassword("NewPass456@");
        dto.setConfirmNewPassword("NewPass456@");

        when(userRepository.findByEmailIgnoreCase("student01@talentlink.demo")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("OldPass123@", "$2a$10$oldPasswordHash")).thenReturn(true);
        when(passwordEncoder.encode("NewPass456@")).thenReturn("$2a$10$newPasswordHash");

        Optional<String> result = passwordService.changePassword("student01@talentlink.demo", dto);

        assertTrue(result.isEmpty());
        assertEquals("$2a$10$newPasswordHash", user.getPasswordHash());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("5. Đổi mật khẩu thất bại (Trường hợp lỗi): Nhập sai mật khẩu cũ thì không cho đổi")
    void changePassword_WrongCurrentPassword_ReturnsErrorAndDoesNotUpdateUser() {
        User user = new User();
        user.setId(10L);
        user.setEmail("student01@talentlink.demo");
        user.setPasswordHash("$2a$10$oldPasswordHash");

        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setCurrentPassword("WrongOldPass!");
        dto.setNewPassword("NewPass456@");
        dto.setConfirmNewPassword("NewPass456@");

        when(userRepository.findByEmailIgnoreCase("student01@talentlink.demo")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongOldPass!", "$2a$10$oldPasswordHash")).thenReturn(false);

        Optional<String> result = passwordService.changePassword("student01@talentlink.demo", dto);

        assertTrue(result.isPresent());
        assertTrue(result.get().contains("Mật khẩu cũ không chính xác"));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("6. Quên mật khẩu: Sinh mã OTP 6 số lưu DB và có thời hạn 10 phút")
    void generateAndLogOtp_ValidEmail_CreatesSixDigitOtpExpiringInTenMinutes() {
        User user = new User();
        user.setId(15L);
        user.setEmail("student01@talentlink.demo");

        when(userRepository.findByEmailIgnoreCase("student01@talentlink.demo")).thenReturn(Optional.of(user));
        when(otpRepository.findByEmailIgnoreCaseAndUsedFalseAndInvalidatedFalse("student01@talentlink.demo"))
                .thenReturn(Collections.emptyList());

        Optional<String> error = passwordService.generateAndLogOtp("student01@talentlink.demo");

        assertTrue(error.isEmpty());
        ArgumentCaptor<PasswordResetOtp> otpCaptor = ArgumentCaptor.forClass(PasswordResetOtp.class);
        verify(otpRepository).save(otpCaptor.capture());

        PasswordResetOtp savedOtp = otpCaptor.getValue();
        assertNotNull(savedOtp.getOtpCode());
        assertTrue(savedOtp.getOtpCode().matches("^\\d{6}$"));
        assertFalse(savedOtp.isUsed());
        assertFalse(savedOtp.isInvalidated());
        assertEquals(0, savedOtp.getAttemptCount());
        assertTrue(savedOtp.getExpiresAt().isAfter(LocalDateTime.now().plusMinutes(9)));
    }

    @Test
    @DisplayName("7. Đặt lại mật khẩu bằng OTP (Trường hợp lỗi): Nhập sai mã OTP lần thứ 5 thì tự động hủy mã")
    void resetPasswordWithOtp_FifthFailedAttempt_InvalidatesOtpAndRejectsReset() {
        User user = new User();
        user.setId(15L);
        user.setEmail("student01@talentlink.demo");

        PasswordResetOtp otp = new PasswordResetOtp();
        otp.setUserId(15L);
        otp.setEmail("student01@talentlink.demo");
        otp.setOtpCode("123456");
        otp.setAttemptCount(4); // Đã sai 4 lần trước đó
        otp.setUsed(false);
        otp.setInvalidated(false);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        ResetPasswordDto dto = new ResetPasswordDto();
        dto.setEmail("student01@talentlink.demo");
        dto.setOtpCode("999999"); // Nhập sai lần thứ 5
        dto.setNewPassword("NewPass123@");
        dto.setConfirmNewPassword("NewPass123@");

        when(userRepository.findByEmailIgnoreCase("student01@talentlink.demo")).thenReturn(Optional.of(user));
        when(otpRepository.findFirstByEmailIgnoreCaseOrderByCreatedAtDesc("student01@talentlink.demo"))
                .thenReturn(Optional.of(otp));

        Optional<String> error = passwordService.resetPasswordWithOtp(dto);

        assertTrue(error.isPresent());
        assertEquals(5, otp.getAttemptCount());
        assertTrue(otp.isInvalidated());
        verify(otpRepository).save(otp);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("8. Đặt lại mật khẩu bằng OTP (Trường hợp lỗi): Mã OTP đã dùng 1 lần hoặc hết hạn 10 phút thì từ chối")
    void resetPasswordWithOtp_ExpiredOtp_MarksInvalidatedAndReturnsError() {
        User user = new User();
        user.setId(15L);
        user.setEmail("student01@talentlink.demo");

        PasswordResetOtp expiredOtp = new PasswordResetOtp();
        expiredOtp.setUserId(15L);
        expiredOtp.setEmail("student01@talentlink.demo");
        expiredOtp.setOtpCode("654321");
        expiredOtp.setUsed(false);
        expiredOtp.setInvalidated(false);
        expiredOtp.setExpiresAt(LocalDateTime.now().minusMinutes(1)); // Đã quá 10 phút

        ResetPasswordDto dto = new ResetPasswordDto();
        dto.setEmail("student01@talentlink.demo");
        dto.setOtpCode("654321");
        dto.setNewPassword("NewPass123@");
        dto.setConfirmNewPassword("NewPass123@");

        when(userRepository.findByEmailIgnoreCase("student01@talentlink.demo")).thenReturn(Optional.of(user));
        when(otpRepository.findFirstByEmailIgnoreCaseOrderByCreatedAtDesc("student01@talentlink.demo"))
                .thenReturn(Optional.of(expiredOtp));

        Optional<String> error = passwordService.resetPasswordWithOtp(dto);

        assertTrue(error.isPresent());
        assertTrue(error.get().contains("hết hạn"));
        assertTrue(expiredOtp.isInvalidated());
        verify(userRepository, never()).save(any());
    }
}
