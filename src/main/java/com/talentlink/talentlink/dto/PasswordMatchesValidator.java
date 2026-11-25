package com.talentlink.talentlink.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator tự viết kiểm tra mật khẩu và nhập lại mật khẩu khớp nhau,
 * gắn thông báo lỗi trực tiếp vào trường confirmPassword để hiển thị dưới ô nhập.
 */
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, StudentRegisterDto> {

    @Override
    public boolean isValid(StudentRegisterDto dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }
        String password = dto.getPassword();
        String confirmPassword = dto.getConfirmPassword();

        if (password == null || confirmPassword == null || confirmPassword.isBlank()) {
            return true; // Để @NotBlank xử lý khi chưa nhập
        }

        boolean matches = password.equals(confirmPassword);
        if (!matches) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
        }
        return matches;
    }
}
