package com.dodibo.learncore.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum BusinessErrorCode {
    NO_CODE(0,HttpStatus.NOT_IMPLEMENTED, "No Code"),
    INCORRECT_CURRENT_PASSWORD(300,HttpStatus.BAD_REQUEST, "Current password is incorrect"),
    NEW_PASSWORD_DOES_NOT_MATCH(301,HttpStatus.BAD_REQUEST, "The new password does not match"),

    ACCOUNT_LOCKED(302,HttpStatus.FORBIDDEN, "User account is locked"),
    ACCOUNT_DISABLED(303,HttpStatus.FORBIDDEN, "User account is disabled"),
    BAD_CREDENTIALS(304,HttpStatus.FORBIDDEN, "Login and/ or password incorrect"),

    FILE_TOO_LARGE(305, HttpStatus.BAD_REQUEST, "File exceeds maximum allowed size"),
    INVALID_FILE_TYPE(306, HttpStatus.BAD_REQUEST, "Unsupported file type"),
    FILE_UPLOAD_FAILED(500, HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed");

    @Getter
    private final int code;

    @Getter
    private final String description;

    @Getter
    private final HttpStatus httpStatus;

    BusinessErrorCode(int code, HttpStatus httpStatus, String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
