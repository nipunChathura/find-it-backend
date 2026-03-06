package lk.icbt.findit.service;

import lk.icbt.findit.response.ImageUploadResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ImageUploadService {

    /**
     * Validates type (folder), validates file is image, renames with unique name,
     * saves under basePath/images/{type}/{uniqueName}, returns the new filename and relative path.
     */
    ImageUploadResponse upload(MultipartFile file, String type);

    /**
     * Returns the image file as a Resource for the given type and fileName (e.g. profile, uuid.jpg).
     * Type must be one of the allowed types; fileName must not contain path traversal.
     */
    Resource getImage(String type, String fileName);
}
