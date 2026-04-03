package lk.icbt.findit.controller;

import lk.icbt.findit.response.ImageUploadResponse;
import lk.icbt.findit.service.ImageUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageUploadController {

    private final ImageUploadService imageUploadService;

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT', 'USER', 'CUSTOMER')")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ImageUploadResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type) {
        ImageUploadResponse result = imageUploadService.upload(file, type);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT', 'USER', 'CUSTOMER')")
    @GetMapping(value = "/show", produces = { MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE, "image/webp" })
    @ResponseBody
    public ResponseEntity<Resource> showImage(
            @RequestParam("type") String type,
            @RequestParam("fileName") String fileName) {
        Resource resource = imageUploadService.getImage(type, fileName);
        String contentType = getContentTypeFromFileName(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CACHE_CONTROL, "max-age=86400")
                .body(resource);
    }

    private static String getContentTypeFromFileName(String fileName) {
        if (fileName == null) return MediaType.IMAGE_JPEG_VALUE;
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".png")) return MediaType.IMAGE_PNG_VALUE;
        if (lower.endsWith(".gif")) return MediaType.IMAGE_GIF_VALUE;
        if (lower.endsWith(".webp")) return "image/webp";
        return MediaType.IMAGE_JPEG_VALUE;
    }
}
