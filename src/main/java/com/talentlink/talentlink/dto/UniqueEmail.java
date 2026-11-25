package com.talentlink.talentlink.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom Bean Validation annotation kiểm tra email chưa được sử dụng trong hệ thống.
 */
@Documented
@Constraint(validatedBy = UniqueEmailValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEmail {

    String message() default "Email này đã được sử dụng. Vui lòng chọn email khác";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
