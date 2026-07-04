package com.dodibo.learncore.exception;

import com.dodibo.learncore.handler.BusinessErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FileUploadException extends RuntimeException {

    private final BusinessErrorCode errorCode;
    private final HttpStatus httpStatus;

    public FileUploadException(String message, BusinessErrorCode errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public FileUploadException(String message, BusinessErrorCode errorCode, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}
