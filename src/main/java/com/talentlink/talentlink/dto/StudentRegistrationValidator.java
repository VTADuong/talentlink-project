package com.talentlink.talentlink.dto;

import com.talentlink.talentlink.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Spring Validator tự viết bổ sung kiểm tra email chưa dùng và mật khẩu nhập lại khớp nhau.
 */
@Component
public class StudentRegistrationValidator implements Validator {

    private final UserRepository userRepository;

    public StudentRegistrationValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return StudentRegisterDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        StudentRegisterDto dto = (StudentRegisterDto) target;

        if (!errors.hasFieldErrors("email") && dto.getEmail() != null && !dto.getEmail().isBlank()) {
            if (userRepository.findByEmailIgnoreCase(dto.getEmail().trim()).isPresent()) {
                errors.rejectValue("email", "email.duplicate",
                        "Email này đã được sử dụng. Vui lòng chọn email khác");
            }
        }

        if (!errors.hasFieldErrors("confirmPassword")
                && dto.getPassword() != null
                && dto.getConfirmPassword() != null
                && !dto.getConfirmPassword().isBlank()) {
            if (!dto.getPassword().equals(dto.getConfirmPassword())) {
                errors.rejectValue("confirmPassword", "password.mismatch",
                        "Mật khẩu nhập lại không khớp với mật khẩu đã nhập");
            }
        }
    }
}
