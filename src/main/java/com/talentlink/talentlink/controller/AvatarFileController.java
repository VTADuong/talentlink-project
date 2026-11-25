package com.talentlink.talentlink.controller;

import com.talentlink.talentlink.service.AvatarService;
import java.util.concurrent.TimeUnit;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controller phục vụ hiển thị file Ảnh đại diện đã tải lên (Task T4E7AA9).
 */
@Controller
public class AvatarFileController {

    private final AvatarService avatarService;

    public AvatarFileController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @GetMapping("/avatars/{fileName:.+}")
    public ResponseEntity<Resource> serveAvatarFile(@PathVariable("fileName") String fileName) {
        Resource resource = avatarService.loadAvatarResource(fileName);
        MediaType mediaType = avatarService.resolveAvatarMediaType(fileName);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
