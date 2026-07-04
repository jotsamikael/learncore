package com.dodibo.learncore.auth;


import com.dodibo.learncore.auth.dto.AuthenticationRequest;
import com.dodibo.learncore.auth.dto.AuthenticationResponse;
import com.dodibo.learncore.auth.dto.ChangePasswordRequest;
import com.dodibo.learncore.auth.dto.ForgotPasswordRequest;
import com.dodibo.learncore.auth.dto.RefreshTokenRequest;
import com.dodibo.learncore.auth.dto.ResendActivationRequest;
import com.dodibo.learncore.auth.dto.ResetPasswordRequest;
import com.dodibo.learncore.auth.dto.StudentRegistrationRequest;
import com.dodibo.learncore.common.EmailUtils;
import com.dodibo.learncore.common.tenant.AuthScope;
import com.dodibo.learncore.common.tenant.TenantContext;
import com.dodibo.learncore.email.EmailService;
import com.dodibo.learncore.email.EmailTemplateName;
import com.dodibo.learncore.exception.OperationNotPermittedException;
import com.dodibo.learncore.exception.ResourceNotFoundException;
import com.dodibo.learncore.role.RoleNames;
import com.dodibo.learncore.role.RoleRepository;
import com.dodibo.learncore.security.JwtService;
import com.dodibo.learncore.security.SecurityUtils;
import com.dodibo.learncore.security.TokenHashUtils;
import com.dodibo.learncore.staff.Staff;
import com.dodibo.learncore.staff.StaffRepository;
import com.dodibo.learncore.student.Student;
import com.dodibo.learncore.student.StudentRepository;
import com.dodibo.learncore.tenant.Tenant;
import com.dodibo.learncore.tenant.TenantService;
import com.dodibo.learncore.user.RefreshToken;
import com.dodibo.learncore.user.RefreshTokenRepository;
import com.dodibo.learncore.user.Token;
import com.dodibo.learncore.user.TokenPurpose;
import com.dodibo.learncore.user.TokenRepository;
import com.dodibo.learncore.user.User;
import com.dodibo.learncore.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.jsonwebtoken.JwtException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
/**
 * AuthenticationService is a service class that handles the authentication of users.
 * It is responsible for registering students, activating accounts, logging in students, staff, and super admins,
 * refreshing access tokens, and building authentication responses.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TenantService tenantService;
    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    @Value("${application.mailing.frontend.reset-password-url}")
    private String resetPasswordUrl;

    @Transactional
    public void registerStudent(String tenantSlug, StudentRegistrationRequest request) throws MessagingException {
        Tenant tenant = tenantService.getBySlug(tenantSlug);
        String email = EmailUtils.normalize(request.getEmail());

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new OperationNotPermittedException("An account with this email already exists");
        }

        String username = request.getUsername().trim();
        if (studentRepository.existsByUsernameAndTenant_Id(username, tenant.getId())) {
            throw new OperationNotPermittedException("Username is already taken");
        }

        var studentRole = roleRepository.findByNameAndTenantIsNull(RoleNames.STUDENT)
                .orElseThrow(() -> new IllegalStateException("STUDENT role was not initialized"));

        var student = Student.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(email)
                .username(username)
                .dateOfBirth(request.getDateOfBirth() != null ? request.getDateOfBirth().toString() : null)
                .password(passwordEncoder.encode(request.getPassword()))
                .tenant(tenant)
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(studentRole))
                .createdDate(LocalDateTime.now())
                .build();

        userRepository.save(student);
        sendValidationEmail(student, tenant);
    }

    @Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByTokenAndPurpose(token, TokenPurpose.ACCOUNT_ACTIVATION)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid activation token"));

        if (savedToken.getValidatedAt() != null) {
            throw new OperationNotPermittedException("Activation token has already been used");
        }

        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser(), savedToken.getUser().getTenant());
            throw new OperationNotPermittedException("Activation token has expired. A new token has been sent.");
        }

        User user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setEnabled(true);
        userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }

    @Transactional
    public AuthenticationResponse loginStudent(String tenantSlug, AuthenticationRequest request) {
        Tenant tenant = tenantService.getBySlug(tenantSlug);
        String email = EmailUtils.normalize(request.getEmail());
        TenantContext.setTenantId(tenant.getId());
        TenantContext.setAuthScope(AuthScope.TENANT_USER);
        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );
            User user = (User) auth.getPrincipal();
            if (!(user instanceof Student)) {
                throw new OperationNotPermittedException("Not a student account");
            }
            return buildResponse(user);
        } finally {
            TenantContext.clear();
        }
    }

    @Transactional
    public AuthenticationResponse loginStaff(AuthenticationRequest request) {
        String email = EmailUtils.normalize(request.getEmail());
        Staff staff = staffRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (staff.isPlatformStaff()) {
            TenantContext.setAuthScope(AuthScope.PLATFORM_STAFF);
        } else {
            TenantContext.setTenantId(staff.getTenantId());
            TenantContext.setAuthScope(AuthScope.TENANT_USER);
        }

        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );
            User user = (User) auth.getPrincipal();
            if (!(user instanceof Staff)) {
                throw new BadCredentialsException("Invalid credentials");
            }
            return buildResponse(user);
        } finally {
            TenantContext.clear();
        }
    }

    @Transactional
    public AuthenticationResponse loginSuperAdmin(AuthenticationRequest request) {
        String email = EmailUtils.normalize(request.getEmail());
        TenantContext.setAuthScope(AuthScope.PLATFORM_STAFF);
        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );
            User user = (User) auth.getPrincipal();
            if (!(user instanceof Staff) || !hasRole(user, RoleNames.SUPER_ADMIN)) {
                throw new OperationNotPermittedException("Not a super admin account");
            }
            return buildResponse(user);
        } finally {
            TenantContext.clear();
        }
    }

    public AuthenticationResponse refreshAccessToken(RefreshTokenRequest request) {
        String refreshTokenValue = request.getRefreshToken();
        String tokenHash = TokenHashUtils.sha256(refreshTokenValue);

        RefreshToken storedToken = refreshTokenRepository.findByTokenHashAndRevokedFalse(tokenHash)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        if (LocalDateTime.now().isAfter(storedToken.getExpiresAt())) {
            storedToken.setRevoked(true);
            refreshTokenRepository.save(storedToken);
            throw new BadCredentialsException("Refresh token has expired");
        }

        try {
            if (!jwtService.isRefreshToken(refreshTokenValue)) {
                throw new BadCredentialsException("Invalid refresh token");
            }
        } catch (JwtException ex) {
            storedToken.setRevoked(true);
            refreshTokenRepository.save(storedToken);
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        User user = userRepository.findById(storedToken.getUser().getId())
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        if (!user.isEnabled()) {
            throw new BadCredentialsException("Account is disabled");
        }

        Long jwtUserId = jwtService.extractUserId(refreshTokenValue);
        if (jwtUserId == null || !jwtUserId.equals(user.getId())) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        String accessToken = jwtService.generateAccessToken(user);
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .token(accessToken)
                .refreshToken(refreshTokenValue)
                .tenantUuid(user.getTenant() != null ? user.getTenant().getUuid() : null)
                .userUuid(user.getUuid())
                .roles(user.getRoles() == null ? List.of() :
                        user.getRoles().stream().map(r -> r.getName()).toList())
                .build();
    }

    @Transactional
    public void logout(RefreshTokenRequest request) {
        String tokenHash = TokenHashUtils.sha256(request.getRefreshToken());
        RefreshToken storedToken = refreshTokenRepository.findByTokenHashAndRevokedFalse(tokenHash)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));
        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User currentUser = resolveAuthenticatedUser();
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new OperationNotPermittedException("New password must be different from the current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        refreshTokenRepository.deleteByUser_Id(user.getId());
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) throws MessagingException {
        Optional<User> user = userRepository.findByEmailIgnoreCase(EmailUtils.normalize(request.getEmail()));
        if (user.isPresent()) {
            sendPasswordResetEmail(user.get());
        }
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        Token savedToken = tokenRepository.findByTokenAndPurpose(request.getToken(), TokenPurpose.PASSWORD_RESET)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid reset token"));

        if (savedToken.getValidatedAt() != null) {
            throw new OperationNotPermittedException("Reset token has already been used");
        }
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            throw new OperationNotPermittedException("Reset token has expired");
        }

        User user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
        refreshTokenRepository.deleteByUser_Id(user.getId());
    }

    @Transactional
    public void resendActivation(ResendActivationRequest request) throws MessagingException {
        User user = userRepository.findByEmailIgnoreCase(EmailUtils.normalize(request.getEmail()))
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (user.isEnabled()) {
            throw new OperationNotPermittedException("Account is already activated");
        }

        Tenant tenant = user.getTenant();
        if (tenant == null) {
            throw new OperationNotPermittedException("Activation resend is only supported for tenant accounts");
        }

        sendValidationEmail(user, tenant);
    }

    private AuthenticationResponse buildResponse(User user) {
        refreshTokenRepository.deleteByUser_Id(user.getId());

        String accessToken = jwtService.generateAccessToken(user);
        String refreshTokenValue = jwtService.generateRefreshToken(user);

        var refreshToken = RefreshToken.builder()
                .tokenHash(TokenHashUtils.sha256(refreshTokenValue))
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(jwtService.getRefreshTokenExpirationMs() / 1000))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .token(accessToken)
                .refreshToken(refreshTokenValue)
                .tenantUuid(user.getTenant() != null ? user.getTenant().getUuid() : null)
                .userUuid(user.getUuid())
                .roles(user.getRoles() == null ? List.of() :
                        user.getRoles().stream().map(r -> r.getName()).toList())
                .build();
    }

    private void sendValidationEmail(User user, Tenant tenant) throws MessagingException {
        String token = generateAndSaveToken(user, TokenPurpose.ACCOUNT_ACTIVATION);
        emailService.sendEmail(
                user.getEmail(),
                user.fullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl + "?tenant=" + tenant.getSlug(),
                token,
                "Activate your " + tenant.getName() + " account"
        );
    }

    private void sendPasswordResetEmail(User user) throws MessagingException {
        String token = generateAndSaveToken(user, TokenPurpose.PASSWORD_RESET);
        String confirmationUrl = resetPasswordUrl;
        if (user.getTenant() != null) {
            confirmationUrl = resetPasswordUrl + "?tenant=" + user.getTenant().getSlug();
        }
        emailService.sendEmail(
                user.getEmail(),
                user.fullName(),
                EmailTemplateName.RESET_PASSWORD,
                confirmationUrl,
                token,
                "Reset your LearnCore password"
        );
    }

    private String generateAndSaveToken(User user, TokenPurpose purpose) {
        tokenRepository.deleteByUser_IdAndPurpose(user.getId(), purpose);
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .purpose(purpose)
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private User resolveAuthenticatedUser() {
        try {
            return SecurityUtils.getCurrentUser();
        } catch (IllegalStateException ex) {
            throw new OperationNotPermittedException("Authentication required");
        }
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

    private boolean hasRole(User user, String roleName) {
        return user.getRoles() != null && user.getRoles().stream()
                .anyMatch(role -> roleName.equals(role.getName()));
    }
}
