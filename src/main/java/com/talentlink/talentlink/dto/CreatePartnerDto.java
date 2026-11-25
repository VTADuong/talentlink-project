package com.talentlink.talentlink.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO cho chức năng Admin tạo tài khoản Đối tác kèm Tên công ty (Task T4E7AA2).
 */
public class CreatePartnerDto {

    @NotBlank(message = "Vui lòng nhập họ tên người đại diện đối tác")
    @Size(min = 2, max = 100, message = "Họ tên phải từ 2 đến 100 ký tự")
    private String fullName;

    @NotBlank(message = "Vui lòng nhập email đăng nhập cho đối tác")
    @Email(message = "Địa chỉ email không đúng định dạng")
    @Size(max = 150, message = "Email tối đa 150 ký tự")
    @UniqueEmail
    private String email;

    @NotBlank(message = "Vui lòng nhập mật khẩu khởi tạo")
    @Size(min = 8, max = 64, message = "Mật khẩu phải từ 8 đến 64 ký tự")
    private String password;

    @Size(max = 20, message = "Số điện thoại tối đa 20 ký tự")
    private String phone;

    @NotBlank(message = "Vui lòng nhập tên công ty của đối tác")
    @Size(min = 2, max = 150, message = "Tên công ty phải từ 2 đến 150 ký tự")
    private String companyName;

    @Size(max = 255, message = "Website công ty tối đa 255 ký tự")
    private String companyWebsite;

    @Size(max = 255, message = "Địa chỉ công ty tối đa 255 ký tự")
    private String companyAddress;

    public CreatePartnerDto() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyWebsite() {
        return companyWebsite;
    }

    public void setCompanyWebsite(String companyWebsite) {
        this.companyWebsite = companyWebsite;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }
}
