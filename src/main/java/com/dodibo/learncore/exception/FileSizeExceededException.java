package com.dodibo.learncore.exception;

import com.dodibo.learncore.handler.BusinessErrorCode;
import org.springframework.http.HttpStatus;

public class FileSizeExceededException extends FileUploadException {

    public FileSizeExceededException(String message) {
        super(message, BusinessErrorCode.FILE_TOO_LARGE, HttpStatus.BAD_REQUEST);
    }
}
