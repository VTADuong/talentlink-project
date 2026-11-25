package com.talentlink.talentlink.service;

import com.talentlink.talentlink.entity.Profile;
import com.talentlink.talentlink.exception.BusinessException;
import com.talentlink.talentlink.exception.ResourceNotFoundException;
import com.talentlink.talentlink.repository.ProfileRepository;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Dịch vụ Quản lý Ảnh đại diện sinh viên (Task T4E7AA9):
 * - Chỉ nhận ảnh JPG / PNG tối đa 2MB (kiểm tra cả phần mở rộng và Content-Type).
 * - Lưu tên file ngẫu nhiên (UUID) trong thư mục cấu hình (app.upload.avatar-dir).
 * - Thay ảnh đại diện mới thì tự động xóa file ảnh đại diện cũ trên ổ đĩa.
 * - Khi chưa có ảnh đại diện thì sinh chữ cái đầu tên để hiển thị ảnh mặc định.
 */
@Service
public class AvatarService {

    private static final Logger log = LoggerFactory.getLogger(AvatarService.class);

    public static final long MAX_AVATAR_SIZE_BYTES = 2L * 1024L * 1024L; // 2MB
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png"
    );
    private static final String AVATAR_URL_PREFIX = "/avatars/";

    private final ProfileRepository profileRepository;
    private final MyProfileService myProfileService;
    private final Path avatarUploadDir;

    public AvatarService(
            ProfileRepository profileRepository,
            MyProfileService myProfileService,
            @Value("${app.upload.avatar-dir:uploads/avatars}") String avatarDirPath) {
        this.profileRepository = profileRepository;
        this.myProfileService = myProfileService;
        this.avatarUploadDir = Paths.get(avatarDirPath).toAbsolutePath().normalize();
    }

    /**
     * Kiểm tra file ảnh đại diện tải lên:
     * - Không rỗng
     * - Chỉ nhận định dạng JPG / PNG (kiểm tra cả phần mở rộng .jpg/.jpeg/.png và Content-Type image/jpeg, image/png)
     * - Dung lượng tối đa 2MB
     *
     * @return Optional.empty() nếu hợp lệ, hoặc thông báo lỗi tiếng Việt nếu không hợp lệ.
     */
    public Optional<String> validateAvatarFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Optional.of("Vui lòng chọn file ảnh đại diện (JPG hoặc PNG, tối đa 2MB)");
        }

        if (file.getSize() > MAX_AVATAR_SIZE_BYTES) {
            return Optional.of("Dung lượng ảnh đại diện vượt quá giới hạn tối đa 2MB! Vui lòng chọn ảnh nhỏ hơn 2MB");
        }

        String extension = extractExtension(file.getOriginalFilename());
        if (extension.isEmpty() || !ALLOWED_EXTENSIONS.contains(extension)) {
            return Optional.of("Định dạng ảnh không hợp lệ. Chỉ chấp nhận ảnh JPG (.jpg, .jpeg) hoặc PNG (.png)");
        }

        String contentType = file.getContentType() != null
                ? file.getContentType().trim().toLowerCase(Locale.ROOT)
                : "";
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            return Optional.of("Content-Type không hợp lệ (" + contentType + "). Chỉ chấp nhận image/jpeg hoặc image/png");
        }

        if ("png".equals(extension) && !"image/png".equals(contentType)) {
            return Optional.of("File có phần mở rộng .png nhưng Content-Type không phải image/png");
        }
        if (("jpg".equals(extension) || "jpeg".equals(extension))
                && !("image/jpeg".equals(contentType) || "image/jpg".equals(contentType))) {
            return Optional.of("File có phần mở rộng .jpg/.jpeg nhưng Content-Type không phải image/jpeg");
        }

        return Optional.empty();
    }

    /**
     * Lưu ảnh đại diện mới cho sinh viên và tự động xóa file ảnh đại diện cũ (nếu có).
     */
    @Transactional
    public Profile updateMyAvatar(String currentEmail, MultipartFile avatarFile) {
        Optional<String> validationError = validateAvatarFile(avatarFile);
        if (validationError.isPresent()) {
            throw new BusinessException(validationError.get(), "/me/profile");
        }

        Profile myProfile = myProfileService.getOrCreateMyProfile(currentEmail);
        String oldAvatarUrl = myProfile.getAvatarUrl();

        String extension = extractExtension(avatarFile.getOriginalFilename());
        String newStoredFileName = UUID.randomUUID() + "." + extension;

        try {
            Files.createDirectories(this.avatarUploadDir);
            Path targetPath = this.avatarUploadDir.resolve(newStoredFileName).normalize();
            if (!targetPath.startsWith(this.avatarUploadDir)) {
                throw new BusinessException("Đường dẫn lưu ảnh đại diện không hợp lệ!", "/me/profile");
            }
            try (InputStream inputStream = avatarFile.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            log.error("Không thể lưu file ảnh đại diện mới", ex);
            throw new BusinessException("Không thể lưu ảnh đại diện lên hệ thống. Vui lòng thử lại!", "/me/profile");
        }

        // Thay ảnh thì xóa ảnh cũ trên ổ đĩa
        deleteOldAvatarFileIfPresent(oldAvatarUrl);

        myProfile.setAvatarUrl(AVATAR_URL_PREFIX + newStoredFileName);
        myProfile.setUpdatedAt(LocalDateTime.now());
        return profileRepository.save(myProfile);
    }

    /**
     * Xóa ảnh đại diện hiện tại của sinh viên để quay về ảnh mặc định theo chữ cái đầu tên.
     */
    @Transactional
    public void deleteMyAvatar(String currentEmail) {
        Profile myProfile = myProfileService.getOrCreateMyProfile(currentEmail);
        String oldAvatarUrl = myProfile.getAvatarUrl();

        deleteOldAvatarFileIfPresent(oldAvatarUrl);

        myProfile.setAvatarUrl(null);
        myProfile.setUpdatedAt(LocalDateTime.now());
        profileRepository.save(myProfile);
    }

    /**
     * Đọc file ảnh đại diện từ thư mục lưu trữ để trả về cho trình duyệt.
     */
    @Transactional(readOnly = true)
    public Resource loadAvatarResource(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new ResourceNotFoundException("Không tìm thấy ảnh đại diện yêu cầu.");
        }
        String cleanFileName = Paths.get(fileName).getFileName().toString();
        try {
            Path filePath = this.avatarUploadDir.resolve(cleanFileName).normalize();
            if (!filePath.startsWith(this.avatarUploadDir) || !Files.exists(filePath)) {
                throw new ResourceNotFoundException("Không tìm thấy file ảnh đại diện trên hệ thống.");
            }
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new ResourceNotFoundException("Không thể đọc file ảnh đại diện.");
        } catch (IOException ex) {
            throw new ResourceNotFoundException("Lỗi khi đọc file ảnh đại diện.");
        }
    }

    public MediaType resolveAvatarMediaType(String fileName) {
        String ext = extractExtension(fileName);
        if ("png".equals(ext)) {
            return MediaType.IMAGE_PNG;
        }
        return MediaType.IMAGE_JPEG;
    }

    /**
     * Lấy chữ cái đầu tên để hiển thị ảnh đại diện mặc định khi sinh viên chưa tải ảnh lên.
     * Ví dụ: "Vũ Thị Ánh Dương" -> "D", "Nguyễn Văn An" -> "A", "Sinh viên" -> "V".
     */
    public String extractInitialFromName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return "S";
        }
        String trimmed = fullName.trim();
        String[] parts = trimmed.split("\\s+");
        for (int i = parts.length - 1; i >= 0; i--) {
            String token = parts[i];
            for (int j = 0; j < token.length(); j++) {
                char ch = token.charAt(j);
                if (Character.isLetter(ch)) {
                    return String.valueOf(Character.toUpperCase(ch));
                }
            }
        }
        return String.valueOf(Character.toUpperCase(trimmed.charAt(0)));
    }

    private void deleteOldAvatarFileIfPresent(String oldAvatarUrl) {
        if (oldAvatarUrl == null || oldAvatarUrl.isBlank()) {
            return;
        }
        String oldFileName = oldAvatarUrl.startsWith(AVATAR_URL_PREFIX)
                ? oldAvatarUrl.substring(AVATAR_URL_PREFIX.length())
                : Paths.get(oldAvatarUrl).getFileName().toString();

        if (oldFileName.isBlank()) {
            return;
        }

        try {
            Path oldFilePath = this.avatarUploadDir.resolve(Paths.get(oldFileName).getFileName().toString()).normalize();
            if (oldFilePath.startsWith(this.avatarUploadDir)) {
                boolean deleted = Files.deleteIfExists(oldFilePath);
                log.info("[AVATAR] Đã xóa file ảnh đại diện cũ [{}]: {}", oldFileName, deleted);
            }
        } catch (IOException ex) {
            log.warn("Không thể xóa file ảnh đại diện cũ [{}]", oldFileName, ex);
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
}
