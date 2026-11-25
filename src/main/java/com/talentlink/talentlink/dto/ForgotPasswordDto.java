package com.talentlink.talentlink.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO cho Form Quên mật khẩu (nhập email để sinh mã OTP 6 số).
 */
public class ForgotPasswordDto {

    @NotBlank(message = "Vui lòng nhập địa chỉ email tài khoản của bạn")
    @Email(message = "Địa chỉ email không đúng định dạng")
    private String email;

    public ForgotPasswordDto() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
