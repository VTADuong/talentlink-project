package com.talentlink.talentlink.dto;

import com.talentlink.talentlink.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Validator tự viết kiểm tra email chưa tồn tại trong bảng users.
 */
@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private final UserRepository userRepository;

    public UniqueEmailValidator() {
        this.userRepository = null;
    }

    @Autowired
    public UniqueEmailValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank() || userRepository == null) {
            return true; // Để @NotBlank hoặc StudentRegistrationValidator xử lý
        }
        return userRepository.findByEmailIgnoreCase(email.trim()).isEmpty();
    }
}
