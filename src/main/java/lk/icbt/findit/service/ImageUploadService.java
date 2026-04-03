package lk.icbt.findit.service;

import lk.icbt.findit.response.ImageUploadResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ImageUploadService {

    
    ImageUploadResponse upload(MultipartFile file, String type);

    
    Resource getImage(String type, String fileName);
}
