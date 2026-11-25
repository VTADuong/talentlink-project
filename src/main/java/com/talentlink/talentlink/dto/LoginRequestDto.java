package com.talentlink.talentlink.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO cho form đăng nhập bằng Email và Mật khẩu.
 */
public class LoginRequestDto {

    @NotBlank(message = "Vui lòng nhập địa chỉ email")
    @Email(message = "Địa chỉ email không đúng định dạng")
    private String email;

    @NotBlank(message = "Vui lòng nhập mật khẩu")
    private String password;

    private boolean rememberMe;

    public LoginRequestDto() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}
