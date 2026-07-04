package com.dodibo.learncore.fileUpload;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    FileUploadResult uploadImage(MultipartFile file, FilePurpose purpose);
}
