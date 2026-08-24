package com.dodibo.learncore.elearningcore.daily_quiz;

import com.dodibo.learncore.common.tenant.TenantStaffResolver;
import com.dodibo.learncore.elearningcore.daily_quiz.dto.CreateDailyQuizRequest;
import com.dodibo.learncore.elearningcore.daily_quiz.dto.DailyQuizResponse;
import com.dodibo.learncore.elearningcore.daily_quiz.dto.FindDailyQuizQuery;
import com.dodibo.learncore.elearningcore.daily_quiz.dto.UpdateDailyQuizRequest;
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
public class DailyQuizServiceImpl implements DailyQuizService {

    private final DailyQuizRepository dailyQuizRepository;
    private final DailyQuizMapper dailyQuizMapper;
    private final TenantStaffResolver tenantStaffResolver;

    @Override
    @Transactional
    public DailyQuizResponse createDailyQuiz(CreateDailyQuizRequest request) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        if (dailyQuizRepository.findByTenant_IdAndQuizDate(tenant.getId(), request.quizDate()).isPresent()) {
            throw new ResourceAlreadyExistsException(
                    "Daily quiz already exists for date " + request.quizDate());
        }
        DailyQuiz dailyQuiz = dailyQuizMapper.toEntity(request, tenant);
        return dailyQuizMapper.toResponse(dailyQuizRepository.save(dailyQuiz));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DailyQuizResponse> getDailyQuizzes(FindDailyQuizQuery query) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        Specification<DailyQuiz> spec = DailyQuizSpecification.fromQuery(query, tenant.getId());
        return dailyQuizRepository.findAll(spec, query.toPageable())
                .map(dailyQuizMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public DailyQuizResponse getDailyQuiz(String uuid) {
        return dailyQuizMapper.toResponse(requireDailyQuiz(uuid));
    }

    @Override
    @Transactional
    public DailyQuizResponse updateDailyQuiz(String uuid, UpdateDailyQuizRequest request) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        DailyQuiz dailyQuiz = requireDailyQuiz(uuid);
        if (dailyQuizRepository.findByTenant_IdAndQuizDateAndUuidNot(tenant.getId(), request.quizDate(), uuid).isPresent()) {
            throw new ResourceAlreadyExistsException("Daily quiz already exists for date " + request.quizDate());
        }
        dailyQuiz.setQuizDate(request.quizDate());
        return dailyQuizMapper.toResponse(dailyQuizRepository.save(dailyQuiz));
    }

    @Override
    @Transactional
    public void deleteDailyQuiz(String uuid) {
        DailyQuiz dailyQuiz = requireDailyQuiz(uuid);
        dailyQuiz.setDeleted(true);
        dailyQuizRepository.save(dailyQuiz);
    }

    private DailyQuiz requireDailyQuiz(String uuid) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        return dailyQuizRepository.findByUuidAndTenant_IdAndIsDeletedFalse(uuid, tenant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Daily quiz not found: " + uuid));
    }
}
