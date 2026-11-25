package com.talentlink.talentlink.service;

import com.talentlink.talentlink.dto.CertificateDto;
import com.talentlink.talentlink.entity.Certificate;
import com.talentlink.talentlink.entity.Profile;
import com.talentlink.talentlink.entity.User;
import com.talentlink.talentlink.exception.BusinessException;
import com.talentlink.talentlink.exception.ForbiddenOperationException;
import com.talentlink.talentlink.exception.ResourceNotFoundException;
import com.talentlink.talentlink.repository.CertificateRepository;
import com.talentlink.talentlink.repository.ProfileRepository;
import com.talentlink.talentlink.repository.UserRepository;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Dịch vụ Quản lý Chứng chỉ và Tải file minh chứng (Task T4E7AA8):
 * - Chỉ nhận file PDF / JPG / PNG, tối đa 5MB (kiểm tra cả phần mở rộng và Content-Type).
 * - Lưu vào thư mục cấu hình được (app.upload.certificate-dir), tên file sinh ngẫu nhiên (UUID).
 * - Xem / tải file chỉ cho phép Chủ hồ sơ và Admin (người khác bị chặn 403).
 * - Xóa chứng chỉ thì xóa luôn file vật lý trên ổ đĩa.
 */
@Service
public class CertificateService {

    private static final Logger log = LoggerFactory.getLogger(CertificateService.class);

    public static final long MAX_FILE_SIZE_BYTES = 5L * 1024L * 1024L; // 5MB
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "jpg", "jpeg", "png");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "image/jpeg",
            "image/jpg",
            "image/png"
    );

    private final CertificateRepository certificateRepository;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final MyProfileService myProfileService;
    private final Path uploadDir;

    public CertificateService(
            CertificateRepository certificateRepository,
            ProfileRepository profileRepository,
            UserRepository userRepository,
            MyProfileService myProfileService,
            @Value("${app.upload.certificate-dir:uploads/certificates}") String uploadDirPath) {
        this.certificateRepository = certificateRepository;
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.myProfileService = myProfileService;
        this.uploadDir = Paths.get(uploadDirPath).toAbsolutePath().normalize();
    }

    @Transactional
    public List<Certificate> listMyCertificates(String currentEmail) {
        Profile myProfile = myProfileService.getOrCreateMyProfile(currentEmail);
        return certificateRepository.findByProfileIdOrderByIssueDateDescIdDesc(myProfile.getId());
    }

    /**
     * Kiểm tra tính hợp lệ của file minh chứng tải lên:
     * - Không rỗng
     * - Dung lượng <= 5MB
     * - Kiểm tra cả Phần mở rộng (.pdf, .jpg, .jpeg, .png) VÀ Content-Type (application/pdf, image/jpeg, image/png)
     *
     * @return Optional.empty() nếu hợp lệ, hoặc thông báo lỗi cụ thể nếu không hợp lệ.
     */
    public Optional<String> validateEvidenceFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Optional.of("Vui lòng chọn file minh chứng đính kèm (PDF, JPG hoặc PNG)");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            return Optional.of("Dung lượng file minh chứng vượt quá giới hạn tối đa 5MB");
        }

        String originalName = file.getOriginalFilename();
        String extension = extractExtension(originalName);
        if (extension.isEmpty() || !ALLOWED_EXTENSIONS.contains(extension)) {
            return Optional.of("Phần mở rộng file không hợp lệ. Chỉ chấp nhận file .pdf, .jpg, .jpeg hoặc .png");
        }

        String contentType = file.getContentType() != null
                ? file.getContentType().trim().toLowerCase(Locale.ROOT)
                : "";
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            return Optional.of("Content-Type không hợp lệ (" + contentType + "). Chỉ nhận PDF, JPG hoặc PNG");
        }

        // Kiểm tra khớp giữa phần mở rộng và Content-Type
        if ("pdf".equals(extension) && !"application/pdf".equals(contentType)) {
            return Optional.of("File có đuôi .pdf nhưng Content-Type không phải application/pdf");
        }
        if ("png".equals(extension) && !"image/png".equals(contentType)) {
            return Optional.of("File có đuôi .png nhưng Content-Type không phải image/png");
        }
        if (("jpg".equals(extension) || "jpeg".equals(extension))
                && !("image/jpeg".equals(contentType) || "image/jpg".equals(contentType))) {
            return Optional.of("File có đuôi .jpg/.jpeg nhưng Content-Type không phải image/jpeg");
        }

        return Optional.empty();
    }

    /**
     * Tạo mới chứng chỉ và lưu file minh chứng với tên sinh ngẫu nhiên (UUID) vào thư mục cấu hình.
     */
    @Transactional
    public Certificate createCertificate(String currentEmail, CertificateDto dto) {
        MultipartFile file = dto.getEvidenceFile();
        Optional<String> validationError = validateEvidenceFile(file);
        if (validationError.isPresent()) {
            throw new BusinessException(validationError.get(), "/me/profile");
        }

        Profile myProfile = myProfileService.getOrCreateMyProfile(currentEmail);
        String originalFileName = sanitizeOriginalFilename(file.getOriginalFilename());
        String extension = extractExtension(originalFileName);
        String randomStoredFileName = UUID.randomUUID() + "." + extension;

        try {
            Files.createDirectories(this.uploadDir);
            Path targetLocation = this.uploadDir.resolve(randomStoredFileName).normalize();
            if (!targetLocation.startsWith(this.uploadDir)) {
                throw new BusinessException("Đường dẫn lưu file không hợp lệ!", "/me/profile");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            log.error("Không thể lưu file minh chứng chứng chỉ", ex);
            throw new BusinessException("Không thể lưu file minh chứng lên hệ thống. Vui lòng thử lại!", "/me/profile");
        }

        Certificate cert = new Certificate();
        cert.setProfileId(myProfile.getId());
        cert.setCertificateName(dto.getCertificateName().trim());
        cert.setIssuingOrganization(dto.getIssuingOrganization().trim());
        cert.setIssueDate(dto.getIssueDate());
        cert.setStoredFileName(randomStoredFileName);
        cert.setOriginalFileName(originalFileName);
        cert.setContentType(file.getContentType());
        cert.setFileSize(file.getSize());
        cert.setCredentialUrl("/certificates/file/" + randomStoredFileName);
        cert.setCreatedAt(LocalDateTime.now());

        return certificateRepository.save(cert);
    }

    /**
     * Xem / Tải file minh chứng:
     * Chỉ cho phép Chủ hồ sơ và Quản trị viên (ADMIN). Các tài khoản khác bị chặn 403 Forbidden.
     */
    @Transactional(readOnly = true)
    public Certificate getAuthorizedCertificate(Long certificateId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ForbiddenOperationException("Vui lòng đăng nhập để xem file minh chứng!");
        }

        Certificate cert = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Chứng chỉ", certificateId));

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);

        if (isAdmin) {
            return cert;
        }

        String currentEmail = authentication.getName();
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(currentEmail);
        if (userOpt.isPresent()) {
            Optional<Profile> profileOpt = profileRepository.findByUserId(userOpt.get().getId());
            if (profileOpt.isPresent() && profileOpt.get().getId().equals(cert.getProfileId())) {
                return cert;
            }
        }

        throw new ForbiddenOperationException(
                "Chỉ chủ hồ sơ và Quản trị viên mới có quyền xem hoặc tải file minh chứng này!");
    }

    @Transactional(readOnly = true)
    public Resource loadCertificateFileResource(Certificate cert) {
        if (cert.getStoredFileName() == null || cert.getStoredFileName().isBlank()) {
            throw new ResourceNotFoundException("Chứng chỉ này không có file minh chứng đính kèm.");
        }
        try {
            Path filePath = this.uploadDir.resolve(cert.getStoredFileName()).normalize();
            if (!filePath.startsWith(this.uploadDir) || !Files.exists(filePath)) {
                throw new ResourceNotFoundException("Không tìm thấy file minh chứng trên hệ thống lưu trữ.");
            }
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new ResourceNotFoundException("Không thể đọc file minh chứng.");
        } catch (IOException ex) {
            throw new ResourceNotFoundException("Lỗi đọc file minh chứng.");
        }
    }

    /**
     * Xóa chứng chỉ thì xóa luôn file vật lý trên ổ đĩa.
     * Chặn 403 Forbidden nếu xóa chứng chỉ của người khác.
     */
    @Transactional
    public void deleteCertificateAndFile(Long certificateId, String currentEmail) {
        Profile myProfile = myProfileService.getOrCreateMyProfile(currentEmail);
        Certificate cert = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Chứng chỉ", certificateId));

        if (!cert.getProfileId().equals(myProfile.getId())) {
            throw new ForbiddenOperationException(
                    "Bạn không có quyền xóa chứng chỉ của người khác (Mã chứng chỉ #" + certificateId + ")!");
        }

        String storedFileName = cert.getStoredFileName();
        certificateRepository.delete(cert);

        if (storedFileName != null && !storedFileName.isBlank()) {
            try {
                Path filePath = this.uploadDir.resolve(storedFileName).normalize();
                if (filePath.startsWith(this.uploadDir)) {
                    boolean deleted = Files.deleteIfExists(filePath);
                    log.info("[XÓA CHỨNG CHỈ] Đã xóa chứng chỉ #{} và xóa file vật lý [{}]: {}",
                            certificateId, storedFileName, deleted);
                }
            } catch (IOException ex) {
                log.warn("Không thể xóa file vật lý [{}] khi xóa chứng chỉ #{}", storedFileName, certificateId, ex);
            }
        }
    }

    private String extractExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return "";
        }
        String clean = Paths.get(filename).getFileName().toString();
        int dotIndex = clean.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == clean.length() - 1) {
            return "";
        }
        return clean.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }

    private String sanitizeOriginalFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "certificate";
        }
        return Paths.get(filename).getFileName().toString().replaceAll("[^a-zA-Z0-9._\\-() ]", "_");
    }
}
