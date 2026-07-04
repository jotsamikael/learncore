package com.dodibo.learncore.fileUpload;

import com.cloudinary.Cloudinary;
import com.dodibo.learncore.exception.FileUploadFailedException;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    @Resource
    private Cloudinary cloudinary;

    private final ImageFileValidator imageFileValidator;
    private final FileUploadProperties properties;

    @Override
    public FileUploadResult uploadImage(MultipartFile file, FilePurpose purpose) {
        imageFileValidator.validate(file);

        String folder = CloudinaryFolders.forPurpose(purpose, properties.getRootFolder());
        try {
            Map<String, Object> options = new HashMap<>();
            options.put("folder", folder);
            options.put("public_id", UUID.randomUUID().toString());
            options.put("resource_type", "image");

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadedFile = cloudinary.uploader().upload(file.getBytes(), options);
            String publicId = (String) uploadedFile.get("public_id");
            String secureUrl = (String) uploadedFile.get("secure_url");

            return new FileUploadResult(
                    secureUrl != null ? secureUrl : cloudinary.url().secure(true).generate(publicId),
                    publicId,
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize()
            );
        } catch (IOException ex) {
            throw new FileUploadFailedException("Failed to upload image to Cloudinary", ex);
        }
    }
}
