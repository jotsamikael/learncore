package com.dodibo.learncore.fileUpload;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "application.file.upload")
public class FileUploadProperties {

    private String rootFolder = "LEARNCORE";

    private ImageProperties image = new ImageProperties();

    @Getter
    @Setter
    public static class ImageProperties {
        private long maxSizeBytes = 5 * 1024 * 1024;
        private List<String> allowedContentTypes = new ArrayList<>(List.of(
                "image/jpeg",
                "image/jpg",
                "image/png",
                "image/webp"
        ));
        private List<String> allowedExtensions = new ArrayList<>(List.of(
                "jpeg",
                "jpg",
                "png",
                "webp"
        ));
    }
}
