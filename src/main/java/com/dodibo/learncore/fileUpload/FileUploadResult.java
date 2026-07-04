package com.dodibo.learncore.fileUpload;

public record FileUploadResult(
        String url,
        String publicId,
        String originalFilename,
        String mimeType,
        long size
) {
}
