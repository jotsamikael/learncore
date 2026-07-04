package com.dodibo.learncore.exception;

import com.dodibo.learncore.handler.BusinessErrorCode;
import org.springframework.http.HttpStatus;

public class FileUploadFailedException extends FileUploadException {

    public FileUploadFailedException(String message, Throwable cause) {
        super(message, BusinessErrorCode.FILE_UPLOAD_FAILED, HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}
