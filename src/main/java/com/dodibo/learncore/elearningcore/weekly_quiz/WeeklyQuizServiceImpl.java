package com.dodibo.learncore.elearningcore.weekly_quiz;

import com.dodibo.learncore.common.tenant.TenantStaffResolver;
import com.dodibo.learncore.elearningcore.weekly_quiz.dto.CreateWeeklyQuizRequest;
import com.dodibo.learncore.elearningcore.weekly_quiz.dto.FindWeeklyQuizQuery;
import com.dodibo.learncore.elearningcore.weekly_quiz.dto.UpdateWeeklyQuizRequest;
import com.dodibo.learncore.elearningcore.weekly_quiz.dto.WeeklyQuizResponse;
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
public class WeeklyQuizServiceImpl implements WeeklyQuizService {

    private final WeeklyQuizRepository weeklyQuizRepository;
    private final WeeklyQuizMapper weeklyQuizMapper;
    private final TenantStaffResolver tenantStaffResolver;

    @Override
    @Transactional
    public WeeklyQuizResponse createWeeklyQuiz(CreateWeeklyQuizRequest request) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        if (weeklyQuizRepository.findByTenant_IdAndWeekStart(tenant.getId(), request.weekStart()).isPresent()) {
            throw new ResourceAlreadyExistsException(
                    "Weekly quiz already exists for week starting " + request.weekStart());
        }
        WeeklyQuiz weeklyQuiz = weeklyQuizMapper.toEntity(request, tenant);
        return weeklyQuizMapper.toResponse(weeklyQuizRepository.save(weeklyQuiz));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WeeklyQuizResponse> getWeeklyQuizzes(FindWeeklyQuizQuery query) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        Specification<WeeklyQuiz> spec = WeeklyQuizSpecification.fromQuery(query, tenant.getId());
        return weeklyQuizRepository.findAll(spec, query.toPageable())
                .map(weeklyQuizMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public WeeklyQuizResponse getWeeklyQuiz(String uuid) {
        return weeklyQuizMapper.toResponse(requireWeeklyQuiz(uuid));
    }

    @Override
    @Transactional
    public WeeklyQuizResponse updateWeeklyQuiz(String uuid, UpdateWeeklyQuizRequest request) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        WeeklyQuiz weeklyQuiz = requireWeeklyQuiz(uuid);
        if (weeklyQuizRepository.findByTenant_IdAndWeekStartAndUuidNot(tenant.getId(), request.weekStart(), uuid).isPresent()) {
            throw new ResourceAlreadyExistsException("Weekly quiz already exists for week starting " + request.weekStart());
        }
        weeklyQuiz.setWeekStart(request.weekStart());
        return weeklyQuizMapper.toResponse(weeklyQuizRepository.save(weeklyQuiz));
    }

    @Override
    @Transactional
    public void deleteWeeklyQuiz(String uuid) {
        WeeklyQuiz weeklyQuiz = requireWeeklyQuiz(uuid);
        weeklyQuiz.setDeleted(true);
        weeklyQuizRepository.save(weeklyQuiz);
    }

    private WeeklyQuiz requireWeeklyQuiz(String uuid) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        return weeklyQuizRepository.findByUuidAndTenant_IdAndIsDeletedFalse(uuid, tenant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Weekly quiz not found: " + uuid));
    }
}
