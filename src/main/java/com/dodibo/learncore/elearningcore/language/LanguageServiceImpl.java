package com.dodibo.learncore.elearningcore.language;

import com.dodibo.learncore.elearningcore.language.dto.CreateLanguageRequest;
import com.dodibo.learncore.elearningcore.language.dto.FindLanguageQuery;
import com.dodibo.learncore.elearningcore.language.dto.LanguageResponse;
import com.dodibo.learncore.exception.OperationNotPermittedException;
import com.dodibo.learncore.exception.ResourceNotFoundException;
import com.dodibo.learncore.exception.ResourceAlreadyExistsException;
import com.dodibo.learncore.security.SecurityUtils;
import com.dodibo.learncore.staff.Staff;
import com.dodibo.learncore.staff.StaffSpecification;
import com.dodibo.learncore.tenant.Tenant;
import com.dodibo.learncore.tenant.TenantRepository;
import com.dodibo.learncore.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository languageRepository;
    private final LanguageMapper languageMapper;
    private final TenantRepository tenantRepository;

    @Override
    public LanguageResponse createLanguage(CreateLanguageRequest createLanguageRequest) {
        //get tenant id
        Tenant tenant = resolveCallerTenant();
        //check if a language with this code already exist for this tennant
        if(languageRepository.findByCodeAndTenantId(createLanguageRequest.code(), tenant.getId()).isPresent()) {
            throw new ResourceAlreadyExistsException("Language with code " + createLanguageRequest.code() + " already exists");
        }
        return languageMapper.toResponse(languageRepository.save(languageMapper.toEntity(createLanguageRequest, tenant.getId())));
    }

    @Override
    public Page<LanguageResponse> getLanguages(FindLanguageQuery query) {
        //get tenant id
        Tenant tenant = resolveCallerTenant();

        Specification<Language> spec = LanguageSpecification.fromQuery(query, tenant.getId());
        return languageRepository.findAll(spec, query.toPageable())
                .map(languageMapper::toResponse);
    }



    //Get Tennant from current User
    private Tenant resolveCallerTenant() {
        User user = SecurityUtils.getCurrentUser();
        if (!(user instanceof Staff staff) || !staff.isTenantStaff()) {
            throw new OperationNotPermittedException("Only tenant staff can manage categories");
        }
        return tenantRepository.findById(staff.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
    }
}
