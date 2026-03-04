package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.response.ImageUploadResponse;
import lk.icbt.findit.service.ImageUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageUploadServiceImpl implements ImageUploadService {

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    @Value("${findit.upload.base-path:./images}")
    private String basePath;

    @Value("${findit.upload.allowed-types:profile,items,receipt}")
    private String allowedTypesConfig;

    @Override
    public ImageUploadResponse upload(MultipartFile file, String type) {
        if (file == null || file.isEmpty()) {
            throw new InvalidRequestException(ResponseCodes.IMAGE_UPLOAD_INVALID_FILE_CODE,
                    "Image file is required");
        }

        Set<String> allowedTypes = Arrays.stream(allowedTypesConfig.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        if (type == null || type.isBlank()) {
            throw new InvalidRequestException(ResponseCodes.MISSING_PARAMETER_CODE,
                    "Type is required (e.g. profile, items, receipt)");
        }

        String typeNormalized = type.trim().toLowerCase();
        if (!allowedTypes.contains(typeNormalized)) {
            throw new InvalidRequestException(ResponseCodes.IMAGE_UPLOAD_INVALID_TYPE_CODE,
                    "Invalid type. Allowed: " + String.join(", ", allowedTypes));
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidRequestException(ResponseCodes.IMAGE_UPLOAD_INVALID_FILE_CODE,
                    "File must be an image (JPEG, PNG, GIF, WebP)");
        }

        String extension = getExtension(file.getOriginalFilename(), contentType);
        String uniqueFileName = UUID.randomUUID().toString() + extension;

        Path typeDir = Paths.get(basePath).resolve(typeNormalized).toAbsolutePath().normalize();
        try {
            Files.createDirectories(typeDir);
        } catch (IOException e) {
            log.error("Failed to create upload directory: {}", typeDir, e);
            throw new InvalidRequestException(ResponseCodes.FAILED_CODE, "Failed to create upload directory");
        }

        Path targetFile = typeDir.resolve(uniqueFileName).normalize();
        if (!targetFile.startsWith(typeDir)) {
            throw new InvalidRequestException(ResponseCodes.IMAGE_UPLOAD_INVALID_FILE_CODE, "Invalid file path");
        }

        try {
            file.transferTo(targetFile.toFile());
        } catch (IOException e) {
            log.error("Failed to save file: {}", targetFile, e);
            throw new InvalidRequestException(ResponseCodes.FAILED_CODE, "Failed to save image");
        }

        String relativePath = typeNormalized + "/" + uniqueFileName;
        ImageUploadResponse response = new ImageUploadResponse();
        response.setStatus(ResponseStatus.SUCCESS.getStatus());
        response.setResponseCode(ResponseCodes.SUCCESS_CODE);
        response.setResponseMessage("Image uploaded successfully.");
        response.setFileName(uniqueFileName);
        response.setType(typeNormalized);
        response.setRelativePath(relativePath);
        return response;
    }

    @Override
    public Resource getImage(String type, String fileName) {
        if (type == null || type.isBlank() || fileName == null || fileName.isBlank()) {
            throw new InvalidRequestException(ResponseCodes.MISSING_PARAMETER_CODE, "Type and fileName are required");
        }
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            throw new InvalidRequestException(ResponseCodes.IMAGE_UPLOAD_INVALID_FILE_CODE, "Invalid fileName");
        }
        Set<String> allowedTypes = Arrays.stream(allowedTypesConfig.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
        String typeNormalized = type.trim().toLowerCase();
        if (!allowedTypes.contains(typeNormalized)) {
            throw new InvalidRequestException(ResponseCodes.IMAGE_UPLOAD_INVALID_TYPE_CODE,
                    "Invalid type. Allowed: " + String.join(", ", allowedTypes));
        }
        Path typeDir = Paths.get(basePath).resolve(typeNormalized).toAbsolutePath().normalize();
        Path targetFile = typeDir.resolve(fileName).normalize();
        if (!targetFile.startsWith(typeDir)) {
            throw new InvalidRequestException(ResponseCodes.IMAGE_UPLOAD_INVALID_FILE_CODE, "Invalid path");
        }
        if (!Files.exists(targetFile) || !Files.isRegularFile(targetFile)) {
            throw new InvalidRequestException(ResponseCodes.IMAGE_UPLOAD_INVALID_FILE_CODE, "Image not found");
        }
        try {
            Resource resource = new UrlResource(targetFile.toUri());
            if (!resource.isReadable()) {
                throw new InvalidRequestException(ResponseCodes.IMAGE_UPLOAD_INVALID_FILE_CODE, "Image not readable");
            }
            return resource;
        } catch (MalformedURLException e) {
            log.error("Invalid file URL: {}", targetFile, e);
            throw new InvalidRequestException(ResponseCodes.FAILED_CODE, "Image not found");
        }
    }

    private static String getExtension(String originalFilename, String contentType) {
        if (originalFilename != null && originalFilename.contains(".")) {
            int last = originalFilename.lastIndexOf('.');
            return originalFilename.substring(last);
        }
        if (contentType != null) {
            if (contentType.contains("jpeg") || contentType.contains("jpg")) return ".jpg";
            if (contentType.contains("png")) return ".png";
            if (contentType.contains("gif")) return ".gif";
            if (contentType.contains("webp")) return ".webp";
        }
        return ".jpg";
    }
}
