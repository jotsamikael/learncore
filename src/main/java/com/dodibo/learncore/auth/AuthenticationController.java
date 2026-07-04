package com.dodibo.learncore.auth;

import com.dodibo.learncore.auth.dto.ActivateAccountRequest;
import com.dodibo.learncore.auth.dto.AuthenticationRequest;
import com.dodibo.learncore.auth.dto.AuthenticationResponse;
import com.dodibo.learncore.auth.dto.ChangePasswordRequest;
import com.dodibo.learncore.auth.dto.ForgotPasswordRequest;
import com.dodibo.learncore.auth.dto.RefreshTokenRequest;
import com.dodibo.learncore.auth.dto.ResendActivationRequest;
import com.dodibo.learncore.auth.dto.ResetPasswordRequest;
import com.dodibo.learncore.auth.dto.StudentRegistrationRequest;
import com.dodibo.learncore.common.tenant.TenantHeader;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthenticationController is a controller class that handles the authentication of users.
 * It is responsible for registering students, activating accounts, logging in students, staff, and super admins,
 * refreshing access tokens, and building authentication responses.
 */
@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Void> register(
            @RequestHeader(TenantHeader.TENANT_SLUG) String tenantSlug,
            @RequestBody @Valid StudentRegistrationRequest request
    ) throws MessagingException {
        service.registerStudent(tenantSlug, request);
        return ResponseEntity.accepted().build();
    }


    @PostMapping("/activate-account")
    public ResponseEntity<Void> activateAccount(@RequestBody @Valid ActivateAccountRequest request)
            throws MessagingException {
        service.activateAccount(request.getToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestHeader(TenantHeader.TENANT_SLUG) String tenantSlug,
            @RequestBody @Valid AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.loginStudent(tenantSlug, request));
    }



    @PostMapping("/staff/login")
    public ResponseEntity<AuthenticationResponse> staffLogin(
            @RequestBody @Valid AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.loginStaff(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(
            @RequestBody @Valid RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(service.refreshAccessToken(request));
    }

    @PostMapping("/superadmin/login")
    public ResponseEntity<AuthenticationResponse> superAdminLogin(
            @RequestBody @Valid AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.loginSuperAdmin(request));
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody @Valid RefreshTokenRequest request) {
        service.logout(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        service.changePassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(
            @RequestBody @Valid ForgotPasswordRequest request
    ) throws MessagingException {
        service.forgotPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        service.resetPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend-activation")
    public ResponseEntity<Void> resendActivation(
            @RequestBody @Valid ResendActivationRequest request
    ) throws MessagingException {
        service.resendActivation(request);
        return ResponseEntity.ok().build();
    }
}
