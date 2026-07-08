package com.dodibo.learncore.tenant;

import com.dodibo.learncore.exception.OperationNotPermittedException;
import com.dodibo.learncore.exception.ResourceNotFoundException;
import com.dodibo.learncore.exception.TenantAccessDeniedException;
import com.dodibo.learncore.fileUpload.FilePurpose;
import com.dodibo.learncore.fileUpload.FileUploadService;
import com.dodibo.learncore.permission.PermissionCodes;
import com.dodibo.learncore.role.Role;
import com.dodibo.learncore.role.RoleSpecification;
import com.dodibo.learncore.security.AuthorizationService;
import com.dodibo.learncore.security.SecurityUtils;
import com.dodibo.learncore.staff.Staff;
import com.dodibo.learncore.tenant.dto.*;
import com.dodibo.learncore.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;
    private final FileUploadService fileUploadService;
    private final AuthorizationService authorizationService;



    @Override
    @Transactional(readOnly = true)
    public Page<TenantResponse> getTenants(FindTenantsQuery query) {
        Specification<Tenant> spec = TenantSpecification.fromQuery(query);
        return tenantRepository.findAll(spec, query.toPageable()).map(tenantMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Tenant getBySlug(String slug) {
        return tenantRepository.findBySlug(slug)
                .filter(Tenant::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + slug));
    }

    @Override
    @Transactional(readOnly = true)
    public Tenant getById(Long id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Tenant getByUuid(String uuid) {
        return tenantRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + uuid));
    }

    @Override
    @Transactional
    public TenantResponse createTenant(CreateTenantRequest request, MultipartFile logo) {
        assertPermission(PermissionCodes.TENANT_CREATE);

        String slug = request.getSlug().trim().toLowerCase();
        if (tenantRepository.existsBySlug(slug)) {
            throw new OperationNotPermittedException("Tenant slug already exists: " + slug);
        }

        String logoUrl = null;
        if (logo != null && !logo.isEmpty()) {
            logoUrl = fileUploadService.uploadImage(logo, FilePurpose.TENANT_LOGO).url();
        }

        Tenant tenant = Tenant.builder()
                .slug(slug)
                .name(request.getName().trim())
                .examFocus(request.getExamFocus())
                .email(trimToNull(request.getEmail()))
                .phone(trimToNull(request.getPhone()))
                .country(trimToNull(request.getCountry()))
                .logoUrl(logoUrl)
                .active(true)
                .createdDate(LocalDateTime.now())
                .build();

        return tenantMapper.toResponse(tenantRepository.save(tenant));
    }

    @Override
    @Transactional
    public TenantResponse updateTenantByPlatform(String tenantUuid, PlatformUpdateTenantRequest request, MultipartFile logo) {
        assertPermission(PermissionCodes.TENANT_UPDATE);
        Tenant tenant = getByUuid(tenantUuid);
        applyTenantProfileUpdate(tenant, request);
        if (logo != null && !logo.isEmpty()) {
            tenant.setLogoUrl(fileUploadService.uploadImage(logo, FilePurpose.TENANT_LOGO).url());
        }
        if (request.getActive() != null) {
            tenant.setActive(request.getActive());
        }
        return tenantMapper.toResponse(tenantRepository.save(tenant));
    }

    @Override
    @Transactional(readOnly = true)
    public TenantResponse getOwnTenant() {
        Tenant tenant = resolveTenantForCurrentStaff(PermissionCodes.ADMIN_TENANT_READ);
        return tenantMapper.toResponse(tenant);
    }

    @Override
    @Transactional
    public TenantResponse updateOwnTenant(UpdateTenantRequest request, MultipartFile logo) {
        Tenant tenant = resolveTenantForCurrentStaff(PermissionCodes.ADMIN_TENANT_UPDATE);
        applyTenantProfileUpdate(tenant, request);
        if (logo != null && !logo.isEmpty()) {
            tenant.setLogoUrl(fileUploadService.uploadImage(logo, FilePurpose.TENANT_LOGO).url());
        }
        return tenantMapper.toResponse(tenantRepository.save(tenant));
    }

    @Override
    @Transactional(readOnly = true)
    public TenantResponse getPublicTenantBySlug(String slug) {
        return tenantMapper.toResponse(getBySlug(slug));
    }

    @Override
    @Transactional
    public TenantResponse changeStatus(String uuid) {
        assertPermission(PermissionCodes.TENANT_UPDATE);
        Tenant tenant = getByUuid(uuid);
        tenant.setActive(!tenant.isActive());
        return tenantMapper.toResponse(tenantRepository.save(tenant));
    }



    private void applyTenantProfileUpdate(Tenant tenant, UpdateTenantRequest request) {
        tenant.setName(request.getName().trim());
        tenant.setExamFocus(request.getExamFocus());
        tenant.setLogoUrl(request.getLogoUrl());
        tenant.setEmail(trimToNull(request.getEmail()));
        tenant.setPhone(trimToNull(request.getPhone()));
        tenant.setCountry(trimToNull(request.getCountry()));
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private Tenant resolveTenantForCurrentStaff(String permission) {
        User user = SecurityUtils.getCurrentUser();
        if (!(user instanceof Staff staff) || !staff.isTenantStaff()) {
            throw new OperationNotPermittedException("Only tenant staff can access this resource");
        }
        if (!authorizationService.can(permission)) {
            throw new OperationNotPermittedException("You are not allowed to manage tenant settings");
        }
        if (staff.getTenantId() == null) {
            throw new TenantAccessDeniedException("No tenant associated with this account");
        }
        return getById(staff.getTenantId());
    }

    private void assertPermission(String permission) {
        if (!authorizationService.can(permission)) {
            throw new OperationNotPermittedException("You are not allowed to perform this action");
        }
    }
}
