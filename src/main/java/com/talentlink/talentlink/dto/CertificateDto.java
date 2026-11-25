package com.talentlink.talentlink.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO cho Form Thêm Chứng chỉ và tải file minh chứng (Task T4E7AA8):
 * - Tên chứng chỉ, Đơn vị cấp, Ngày cấp, File minh chứng (MultipartFile: PDF/JPG/PNG <= 5MB).
 */
public class CertificateDto {

    @NotBlank(message = "Vui lòng nhập tên chứng chỉ")
    @Size(min = 2, max = 150, message = "Tên chứng chỉ phải từ 2 đến 150 ký tự")
    private String certificateName;

    @NotBlank(message = "Vui lòng nhập đơn vị cấp chứng chỉ")
    @Size(min = 2, max = 150, message = "Đơn vị cấp phải từ 2 đến 150 ký tự")
    private String issuingOrganization;

    @NotNull(message = "Vui lòng chọn ngày cấp chứng chỉ")
    @PastOrPresent(message = "Ngày cấp chứng chỉ không được nằm trong tương lai")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate issueDate;

    private MultipartFile evidenceFile;

    public CertificateDto() {
    }

    public String getCertificateName() {
        return certificateName;
    }

    public void setCertificateName(String certificateName) {
        this.certificateName = certificateName;
    }

    public String getIssuingOrganization() {
        return issuingOrganization;
    }

    public void setIssuingOrganization(String issuingOrganization) {
        this.issuingOrganization = issuingOrganization;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public MultipartFile getEvidenceFile() {
        return evidenceFile;
    }

    public void setEvidenceFile(MultipartFile evidenceFile) {
        this.evidenceFile = evidenceFile;
    }
}
