package com.dodibo.learncore.handler;

import com.dodibo.learncore.exception.*;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.MessagingException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

import static com.dodibo.learncore.handler.BusinessErrorCode.*;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleException(ResourceAlreadyExistsException exp){
        return ResponseEntity.status(BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(RESOURCE_ALREADY_EXISTS.getCode())
                        .businessErrorDescription(RESOURCE_ALREADY_EXISTS.getDescription())
                        .error(exp.getMessage())
                        .build()

                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException exp) {
        Set<String> errors = new HashSet<>();
        exp.getBindingResult().getAllErrors()
                .forEach(error -> errors.add(error.getDefaultMessage()));

        return ResponseEntity.status(BAD_REQUEST)
                .body(ExceptionResponse.builder().validationErrors(errors).build());
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(LockedException exp) {
        return ResponseEntity.status(UNAUTHORIZED)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(ACCOUNT_LOCKED.getCode())
                        .businessErrorDescription(ACCOUNT_LOCKED.getDescription())
                        .error(exp.getMessage())
                        .build());
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(DisabledException exp) {
        return ResponseEntity.status(UNAUTHORIZED)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(ACCOUNT_DISABLED.getCode())
                        .businessErrorDescription(ACCOUNT_DISABLED.getDescription())
                        .error(exp.getMessage())
                        .build());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException exp) {
        String message = exp.getMessage() != null ? exp.getMessage() : "Login and / or Password is incorrect";
        return ResponseEntity.status(UNAUTHORIZED)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BAD_CREDENTIALS.getCode())
                        .businessErrorDescription(BAD_CREDENTIALS.getDescription())
                        .error(message)
                        .build());
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ExceptionResponse> handleFileUploadException(FileUploadException exp) {
        return ResponseEntity.status(exp.getHttpStatus())
                .body(ExceptionResponse.builder()
                        .businessErrorCode(exp.getErrorCode().getCode())
                        .businessErrorDescription(exp.getErrorCode().getDescription())
                        .error(exp.getMessage())
                        .build());
    }

    @ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
    public ResponseEntity<ExceptionResponse> handleMaxUploadSizeExceeded(
            org.springframework.web.multipart.MaxUploadSizeExceededException exp
    ) {
        return ResponseEntity.status(BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(FILE_TOO_LARGE.getCode())
                        .businessErrorDescription(FILE_TOO_LARGE.getDescription())
                        .error("Uploaded file exceeds the maximum allowed size")
                        .build());
    }

    @ExceptionHandler(org.springframework.web.multipart.MultipartException.class)
    public ResponseEntity<ExceptionResponse> handleMultipartException(
            org.springframework.web.multipart.MultipartException exp
    ) {
        return ResponseEntity.status(BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(FILE_UPLOAD_FAILED.getCode())
                        .businessErrorDescription(FILE_UPLOAD_FAILED.getDescription())
                        .error(exp.getMostSpecificCause().getMessage())
                        .build());
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleException(MessagingException exp) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponse> handleException(DataIntegrityViolationException exception) {
        return ResponseEntity.status(CONFLICT)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(399)
                        .businessErrorDescription("Duplicated entry " + exception.getMessage())
                        .error("Error occurred")
                        .build());
    }

    @ExceptionHandler(OperationNotPermittedException.class)
    public ResponseEntity<ExceptionResponse> handleException(OperationNotPermittedException exp) {
        return ResponseEntity.status(BAD_REQUEST)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(RoleAssignmentException.class)
    public ResponseEntity<ExceptionResponse> handleException(RoleAssignmentException exp) {
        return ResponseEntity.status(BAD_REQUEST)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler({TenantAccessDeniedException.class, AccessDeniedException.class})
    public ResponseEntity<ExceptionResponse> handleAccessDenied(RuntimeException exp) {
        return ResponseEntity.status(FORBIDDEN)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFound(ResourceNotFoundException exp) {
        return ResponseEntity.status(NOT_FOUND)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exp) {
        exp.printStackTrace();
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.builder()
                        .businessErrorDescription("Internal error, please contact the admin")
                        .error(exp.getMessage())
                        .build());
    }
}
