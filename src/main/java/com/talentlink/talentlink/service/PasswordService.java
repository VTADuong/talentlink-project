package com.talentlink.talentlink.service;

import com.talentlink.talentlink.dto.ChangePasswordDto;
import com.talentlink.talentlink.dto.ResetPasswordDto;
import com.talentlink.talentlink.entity.PasswordResetOtp;
import com.talentlink.talentlink.entity.User;
import com.talentlink.talentlink.repository.PasswordResetOtpRepository;
import com.talentlink.talentlink.repository.UserRepository;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dịch vụ xử lý Đổi mật khẩu và Quên mật khẩu bằng OTP 6 số (Task T4E7AA1):
 * - Đổi mật khẩu: Kiểm tra mật khẩu cũ đúng mới cho đổi.
 * - Quên mật khẩu: Sinh mã OTP 6 số lưu DB, hết hạn sau 10 phút, ghi mã ra log (thay cho gửi mail).
 * - Đặt lại mật khẩu: Mã dùng 1 lần, nhập sai quá 5 lần thì hủy mã.
 */
@Service
public class PasswordService {

    private static final Logger log = LoggerFactory.getLogger(PasswordService.class);
    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final UserRepository userRepository;
    private final PasswordResetOtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordService(
            UserRepository userRepository,
            PasswordResetOtpRepository otpRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Đổi mật khẩu cho người dùng đang đăng nhập.
     *
     * @return Optional.empty() nếu đổi thành công, hoặc thông báo lỗi nếu mật khẩu cũ không đúng.
     */
    @Transactional
    public Optional<String> changePassword(String email, ChangePasswordDto dto) {
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);
        if (userOpt.isEmpty()) {
            return Optional.of("Không tìm thấy tài khoản người dùng.");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPasswordHash())) {
            return Optional.of("Mật khẩu cũ không chính xác. Vui lòng kiểm tra lại.");
        }

        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("[ĐỔI MẬT KHẨU] Người dùng [{}] đã đổi mật khẩu thành công.", user.getEmail());
        return Optional.empty();
    }

    /**
     * Sinh mã OTP 6 số cho chức năng Quên mật khẩu, lưu vào DB (hết hạn sau 10 phút)
     * và ghi rõ mã OTP ra log hệ thống (thay cho gửi email).
     *
     * @return Optional.empty() nếu sinh OTP thành công, hoặc thông báo lỗi nếu email không tồn tại.
     */
    @Transactional
    public Optional<String> generateAndLogOtp(String rawEmail) {
        String email = rawEmail != null ? rawEmail.trim().toLowerCase() : "";
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);
        if (userOpt.isEmpty()) {
            return Optional.of("Không tìm thấy tài khoản nào đăng ký với địa chỉ email này.");
        }

        User user = userOpt.get();
        LocalDateTime now = LocalDateTime.now();

        // Hủy các mã OTP cũ chưa sử dụng của email này
        List<PasswordResetOtp> activeOtps =
                otpRepository.findByEmailIgnoreCaseAndUsedFalseAndInvalidatedFalse(email);
        for (PasswordResetOtp oldOtp : activeOtps) {
            oldOtp.setInvalidated(true);
        }
        otpRepository.saveAll(activeOtps);

        // Sinh mã OTP 6 chữ số ngẫu nhiên (000000 - 999999)
        String otpCode = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
        LocalDateTime expiresAt = now.plusMinutes(OTP_EXPIRY_MINUTES);

        PasswordResetOtp otp = new PasswordResetOtp();
        otp.setUserId(user.getId());
        otp.setEmail(user.getEmail());
        otp.setOtpCode(otpCode);
        otp.setExpiresAt(expiresAt);
        otp.setAttemptCount(0);
        otp.setUsed(false);
        otp.setInvalidated(false);
        otp.setCreatedAt(now);
        otpRepository.save(otp);

        // Ghi mã OTP 6 số ra Log (thay cho gửi mail)
        log.info("==================================================================");
        log.info("[QUÊN MẬT KHẨU - OTP] Email: {} | MÃ OTP 6 SỐ: {} | Hết hạn lúc: {} ({} phút)",
                user.getEmail(), otpCode, expiresAt.format(TIME_FMT), OTP_EXPIRY_MINUTES);
        log.info("==================================================================");

        return Optional.empty();
    }

    /**
     * Xác thực mã OTP 6 số và đặt lại mật khẩu mới.
     * - Mã chỉ dùng 1 lần (used = true).
     * - Hết hạn sau 10 phút.
     * - Nhập sai quá 5 lần thì hủy mã (invalidated = true).
     *
     * @return Optional.empty() nếu đặt lại mật khẩu thành công, hoặc thông báo lỗi cụ thể.
     */
    @Transactional
    public Optional<String> resetPasswordWithOtp(ResetPasswordDto dto) {
        String email = dto.getEmail() != null ? dto.getEmail().trim().toLowerCase() : "";
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);
        if (userOpt.isEmpty()) {
            return Optional.of("Không tìm thấy tài khoản với địa chỉ email này.");
        }

        Optional<PasswordResetOtp> otpOpt =
                otpRepository.findFirstByEmailIgnoreCaseOrderByCreatedAtDesc(email);
        if (otpOpt.isEmpty()) {
            return Optional.of("Chưa có yêu cầu mã OTP cho email này. Vui lòng gửi yêu cầu nhận mã OTP trước.");
        }

        PasswordResetOtp otp = otpOpt.get();
        LocalDateTime now = LocalDateTime.now();

        // 1. Kiểm tra mã đã được sử dụng chưa (Mã dùng một lần)
        if (otp.isUsed()) {
            return Optional.of("Mã OTP này đã được sử dụng (mã chỉ dùng một lần). Vui lòng yêu cầu mã OTP mới.");
        }

        // 2. Kiểm tra mã đã bị hủy do sai quá 5 lần chưa
        if (otp.isInvalidated()) {
            return Optional.of("Mã OTP này đã bị hủy do nhập sai quá 5 lần hoặc đã có mã mới. Vui lòng yêu cầu mã OTP mới.");
        }

        // 3. Kiểm tra thời hạn 10 phút
        if (now.isAfter(otp.getExpiresAt())) {
            otp.setInvalidated(true);
            otpRepository.save(otp);
            return Optional.of("Mã OTP đã hết hạn (quá 10 phút). Vui lòng yêu cầu mã OTP mới.");
        }

        // 4. Kiểm tra khớp mã OTP 6 số
        String submittedCode = dto.getOtpCode() != null ? dto.getOtpCode().trim() : "";
        if (!otp.getOtpCode().equals(submittedCode)) {
            int newAttempts = otp.getAttemptCount() + 1;
            otp.setAttemptCount(newAttempts);

            if (newAttempts >= MAX_FAILED_ATTEMPTS) {
                otp.setInvalidated(true);
                otpRepository.save(otp);
                log.warn("[QUÊN MẬT KHẨU - OTP] Mã OTP của email [{}] đã bị HỦY do nhập sai quá {} lần.",
                        email, MAX_FAILED_ATTEMPTS);
                return Optional.of("Mã OTP không chính xác! Bạn đã nhập sai quá 5 lần nên mã OTP này đã bị hủy. Vui lòng yêu cầu mã mới.");
            }

            otpRepository.save(otp);
            int remaining = MAX_FAILED_ATTEMPTS - newAttempts;
            return Optional.of(String.format(
                    "Mã OTP không chính xác (Đã sai %d/%d lần). Bạn còn %d lần thử trước khi mã bị hủy.",
                    newAttempts, MAX_FAILED_ATTEMPTS, remaining));
        }

        // 5. Mã OTP hợp lệ -> Đánh dấu đã dùng 1 lần và cập nhật mật khẩu BCrypt mới
        otp.setUsed(true);
        otp.setUsedAt(now);
        otpRepository.save(otp);

        User user = userOpt.get();
        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        user.setUpdatedAt(now);
        userRepository.save(user);

        log.info("[QUÊN MẬT KHẨU - OTP] Đặt lại mật khẩu mới thành công cho tài khoản [{}].", email);
        return Optional.empty();
    }
}
