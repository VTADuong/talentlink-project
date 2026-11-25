package com.talentlink.talentlink.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * DTO cho Form Thêm / Sửa Học vấn trên trang hồ sơ (Task T4E7AA4):
 * - Trường (schoolName), Chuyên ngành (major), Từ ngày (startDate) – Đến ngày (endDate),
 *   GPA (tùy chọn, 0.0 – 4.0).
 * - Ngày bắt đầu không sau ngày kết thúc.
 */
public class EducationDto {

    private Long id;

    @NotBlank(message = "Vui lòng nhập tên trường đào tạo")
    @Size(min = 2, max = 150, message = "Tên trường phải từ 2 đến 150 ký tự")
    private String schoolName;

    @NotBlank(message = "Vui lòng nhập chuyên ngành học")
    @Size(min = 2, max = 150, message = "Chuyên ngành phải từ 2 đến 150 ký tự")
    private String major;

    @Size(max = 100, message = "Bằng cấp tối đa 100 ký tự")
    private String degree;

    @NotNull(message = "Vui lòng chọn ngày bắt đầu học")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "Vui lòng chọn ngày kết thúc / dự kiến tốt nghiệp")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @DecimalMin(value = "0.0", inclusive = true, message = "GPA phải nằm trong khoảng từ 0.0 đến 4.0")
    @DecimalMax(value = "4.0", inclusive = true, message = "GPA không được vượt quá 4.0")
    @Digits(integer = 1, fraction = 2, message = "GPA không đúng định dạng (ví dụ: 3.45)")
    private BigDecimal gpa;

    @Size(max = 1000, message = "Mô tả thành tích học tập tối đa 1000 ký tự")
    private String description;

    public EducationDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getGpa() {
        return gpa;
    }

    public void setGpa(BigDecimal gpa) {
        this.gpa = gpa;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
