package com.dodibo.learncore.exception;

import com.dodibo.learncore.handler.BusinessErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidFileTypeException extends FileUploadException {

    public InvalidFileTypeException(String message) {
        super(message, BusinessErrorCode.INVALID_FILE_TYPE, HttpStatus.BAD_REQUEST);
    }
}
