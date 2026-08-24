package com.dodibo.learncore.elearningcore.language;

import com.dodibo.learncore.common.tenant.TenantStaffResolver;
import com.dodibo.learncore.elearningcore.language.dto.CreateLanguageRequest;
import com.dodibo.learncore.elearningcore.language.dto.FindLanguageQuery;
import com.dodibo.learncore.elearningcore.language.dto.LanguageResponse;
import com.dodibo.learncore.elearningcore.language.dto.UpdateLanguageRequest;
import com.dodibo.learncore.exception.ResourceAlreadyExistsException;
import com.dodibo.learncore.exception.ResourceNotFoundException;
import com.dodibo.learncore.tenant.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository languageRepository;
    private final LanguageMapper languageMapper;
    private final TenantStaffResolver tenantStaffResolver;

    @Override
    @Transactional
    public LanguageResponse createLanguage(CreateLanguageRequest createLanguageRequest) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        if (languageRepository.findByCodeAndTenantId(createLanguageRequest.code(), tenant.getId()).isPresent()) {
            throw new ResourceAlreadyExistsException(
                    "Language with code " + createLanguageRequest.code() + " already exists");
        }
        Language language = languageMapper.toEntity(createLanguageRequest, tenant.getId());
        return languageMapper.toResponse(languageRepository.save(language));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LanguageResponse> getLanguages(FindLanguageQuery query) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        Specification<Language> spec = LanguageSpecification.fromQuery(query, tenant.getId());
        return languageRepository.findAll(spec, query.toPageable())
                .map(languageMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public LanguageResponse getLanguage(String uuid) {
        return languageMapper.toResponse(requireLanguage(uuid));
    }

    @Override
    @Transactional
    public LanguageResponse updateLanguage(String uuid, UpdateLanguageRequest request) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        Language language = requireLanguage(uuid);
        if (languageRepository.findByCodeAndTenantIdAndUuidNot(request.code(), tenant.getId(), uuid).isPresent()) {
            throw new ResourceAlreadyExistsException("Language with code " + request.code() + " already exists");
        }
        language.setName(request.name());
        language.setCode(request.code());
        return languageMapper.toResponse(languageRepository.save(language));
    }

    @Override
    @Transactional
    public void deleteLanguage(String uuid) {
        Language language = requireLanguage(uuid);
        language.setDeleted(true);
        languageRepository.save(language);
    }

    private Language requireLanguage(String uuid) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        return languageRepository.findByUuidAndTenantIdAndIsDeletedFalse(uuid, tenant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Language not found: " + uuid));
    }
}
