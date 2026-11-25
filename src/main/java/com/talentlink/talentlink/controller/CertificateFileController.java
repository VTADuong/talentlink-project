package com.talentlink.talentlink.controller;

import com.talentlink.talentlink.entity.Certificate;
import com.talentlink.talentlink.service.CertificateService;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller phục vụ Xem và Tải file minh chứng chứng chỉ (Task T4E7AA8):
 * - Chỉ Chủ hồ sơ và Quản trị viên (ADMIN) mới được phép xem/tải file.
 * - Các tài khoản khác truy cập bị chặn 403 Forbidden.
 */
@Controller
public class CertificateFileController {

    private final CertificateService certificateService;

    public CertificateFileController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping("/certificates/{id}/file")
    public ResponseEntity<Resource> viewOrDownloadCertificateFile(
            @PathVariable("id") Long id,
            @RequestParam(value = "download", defaultValue = "false") boolean download,
            Authentication authentication) {

        Certificate cert = certificateService.getAuthorizedCertificate(id, authentication);
        Resource resource = certificateService.loadCertificateFileResource(cert);

        String contentType = (cert.getContentType() != null && !cert.getContentType().isBlank())
                ? cert.getContentType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        String displayFileName = (cert.getOriginalFileName() != null && !cert.getOriginalFileName().isBlank())
                ? cert.getOriginalFileName()
                : cert.getStoredFileName();

        ContentDisposition disposition = (download ? ContentDisposition.attachment() : ContentDisposition.inline())
                .filename(displayFileName, StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(resource);
    }
}
