package com.talentlink.talentlink.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * DTO cho trang Hồ sơ của tôi (/me/profile — Task T4E7AA3):
 * Xem và sửa: họ tên, ngày sinh, số điện thoại, giới thiệu bản thân,
 * vị trí mong muốn, nơi làm việc mong muốn.
 * Validate định dạng ngày và số điện thoại, hiện lỗi từng trường.
 */
public class MyProfileUpdateDto {

    private Long profileId;

    @NotBlank(message = "Vui lòng nhập họ và tên")
    @Size(min = 2, max = 100, message = "Họ và tên phải có độ dài từ 2 đến 100 ký tự")
    private String fullName;

    @NotNull(message = "Vui lòng nhập ngày sinh đúng định dạng (yyyy-MM-dd)")
    @Past(message = "Ngày sinh phải là một ngày hợp lệ trong quá khứ")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Vui lòng nhập số điện thoại liên hệ")
    @Pattern(
            regexp = "^(0|\\+84)(3|5|7|8|9)\\d{8}$|^0\\d{9}$",
            message = "Số điện thoại không đúng định dạng (phải gồm 10 chữ số bắt đầu bằng 0 hoặc +84)"
    )
    private String phone;

    @NotBlank(message = "Vui lòng nhập vị trí công việc mong muốn")
    @Size(min = 2, max = 150, message = "Vị trí mong muốn phải từ 2 đến 150 ký tự")
    private String desiredPosition;

    @NotBlank(message = "Vui lòng nhập nơi làm việc mong muốn")
    @Size(min = 2, max = 100, message = "Nơi làm việc mong muốn phải từ 2 đến 100 ký tự")
    private String desiredLocation;

    @NotBlank(message = "Vui lòng nhập giới thiệu bản thân")
    @Size(min = 10, max = 2000, message = "Giới thiệu bản thân phải từ 10 đến 2000 ký tự")
    private String bio;

    public MyProfileUpdateDto() {
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDesiredPosition() {
        return desiredPosition;
    }

    public void setDesiredPosition(String desiredPosition) {
        this.desiredPosition = desiredPosition;
    }

    public String getDesiredLocation() {
        return desiredLocation;
    }

    public void setDesiredLocation(String desiredLocation) {
        this.desiredLocation = desiredLocation;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
