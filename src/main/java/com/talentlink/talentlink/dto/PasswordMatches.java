package com.talentlink.talentlink.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom Bean Validation annotation kiểm tra Mật khẩu và Nhập lại mật khẩu phải khớp nhau.
 */
@Documented
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatches {

    String message() default "Mật khẩu nhập lại không khớp với mật khẩu đã nhập";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
