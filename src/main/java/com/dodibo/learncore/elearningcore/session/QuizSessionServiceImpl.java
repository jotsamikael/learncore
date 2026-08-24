package com.dodibo.learncore.elearningcore.session;

import com.dodibo.learncore.common.tenant.TenantMemberResolver;
import com.dodibo.learncore.elearningcore.category.Category;
import com.dodibo.learncore.elearningcore.category.CategoryRepository;
import com.dodibo.learncore.elearningcore.daily_quiz.DailyQuiz;
import com.dodibo.learncore.elearningcore.daily_quiz.DailyQuizRepository;
import com.dodibo.learncore.elearningcore.session.dto.FindQuizSessionQuery;
import com.dodibo.learncore.elearningcore.session.dto.QuizSessionResponse;
import com.dodibo.learncore.elearningcore.session.dto.StartQuizSessionRequest;
import com.dodibo.learncore.elearningcore.weekly_quiz.WeeklyQuiz;
import com.dodibo.learncore.elearningcore.weekly_quiz.WeeklyQuizRepository;
import com.dodibo.learncore.exception.ResourceNotFoundException;
import com.dodibo.learncore.tenant.Tenant;
import com.dodibo.learncore.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class QuizSessionServiceImpl implements QuizSessionService {

    private final QuizSessionRepository quizSessionRepository;
    private final CategoryRepository categoryRepository;
    private final DailyQuizRepository dailyQuizRepository;
    private final WeeklyQuizRepository weeklyQuizRepository;
    private final QuizSessionMapper quizSessionMapper;
    private final TenantMemberResolver tenantMemberResolver;

    @Override
    @Transactional
    public QuizSessionResponse startSession(StartQuizSessionRequest request) {
        Tenant tenant = tenantMemberResolver.requireCallerTenant();
        User user = tenantMemberResolver.requireCurrentUser();
        Category category = resolveCategory(request.categoryUuid(), tenant.getId());
        DailyQuiz dailyQuiz = resolveDailyQuiz(request.dailyQuizUuid(), tenant.getId());
        WeeklyQuiz weeklyQuiz = resolveWeeklyQuiz(request.weeklyQuizUuid(), tenant.getId());
        QuizSession session = quizSessionMapper.toEntity(request, tenant, user, category, dailyQuiz, weeklyQuiz);
        return quizSessionMapper.toResponse(quizSessionRepository.save(session));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuizSessionResponse> getSessions(FindQuizSessionQuery query) {
        Tenant tenant = tenantMemberResolver.requireCallerTenant();
        User user = tenantMemberResolver.requireCurrentUser();
        Specification<QuizSession> spec = QuizSessionSpecification.fromQuery(query, tenant.getId(), user.getId());
        return quizSessionRepository.findAll(spec, query.toPageable())
                .map(quizSessionMapper::toResponse);
    }

    private Category resolveCategory(String categoryUuid, Long tenantId) {
        if (!StringUtils.hasText(categoryUuid)) {
            return null;
        }
        return categoryRepository.findByUuidAndTenantId(categoryUuid, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryUuid));
    }

    private DailyQuiz resolveDailyQuiz(String dailyQuizUuid, Long tenantId) {
        if (!StringUtils.hasText(dailyQuizUuid)) {
            return null;
        }
        return dailyQuizRepository.findByUuidAndTenant_Id(dailyQuizUuid, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Daily quiz not found: " + dailyQuizUuid));
    }

    private WeeklyQuiz resolveWeeklyQuiz(String weeklyQuizUuid, Long tenantId) {
        if (!StringUtils.hasText(weeklyQuizUuid)) {
            return null;
        }
        return weeklyQuizRepository.findByUuidAndTenant_Id(weeklyQuizUuid, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Weekly quiz not found: " + weeklyQuizUuid));
    }
}
