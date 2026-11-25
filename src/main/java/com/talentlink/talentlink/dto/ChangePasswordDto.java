package com.talentlink.talentlink.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO cho Form Đổi mật khẩu (nhập mật khẩu cũ đúng mới cho đổi).
 */
public class ChangePasswordDto {

    @NotBlank(message = "Vui lòng nhập mật khẩu hiện tại")
    private String currentPassword;

    @NotBlank(message = "Vui lòng nhập mật khẩu mới")
    @Size(min = 8, max = 64, message = "Mật khẩu mới phải có từ 8 đến 64 ký tự")
    private String newPassword;

    @NotBlank(message = "Vui lòng nhập lại mật khẩu mới")
    private String confirmNewPassword;

    public ChangePasswordDto() {
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }
}
