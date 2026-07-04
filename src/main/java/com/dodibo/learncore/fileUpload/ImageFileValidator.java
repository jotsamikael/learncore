package com.dodibo.learncore.fileUpload;

import com.dodibo.learncore.exception.FileSizeExceededException;
import com.dodibo.learncore.exception.InvalidFileTypeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ImageFileValidator {

    private final FileUploadProperties properties;

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileTypeException("File is required");
        }

        long maxSize = properties.getImage().getMaxSizeBytes();
        if (file.getSize() > maxSize) {
            throw new FileSizeExceededException(
                    "Image exceeds maximum allowed size of " + (maxSize / (1024 * 1024)) + "MB"
            );
        }

        String contentType = file.getContentType();
        Set<String> allowedContentTypes = properties.getImage().getAllowedContentTypes().stream()
                .map(type -> type.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());

        if (contentType == null || !allowedContentTypes.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new InvalidFileTypeException(
                    "Unsupported image type. Allowed types: " + String.join(", ", allowedContentTypes)
            );
        }

        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        Set<String> allowedExtensions = properties.getImage().getAllowedExtensions().stream()
                .map(ext -> ext.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());

        if (extension == null || !allowedExtensions.contains(extension.toLowerCase(Locale.ROOT))) {
            throw new InvalidFileTypeException(
                    "Unsupported file extension. Allowed extensions: " + String.join(", ", allowedExtensions)
            );
        }
    }
}
