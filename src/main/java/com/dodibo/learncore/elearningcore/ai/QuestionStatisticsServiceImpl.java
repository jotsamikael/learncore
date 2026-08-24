package com.dodibo.learncore.elearningcore.ai;

import com.dodibo.learncore.common.tenant.TenantMemberResolver;
import com.dodibo.learncore.elearningcore.ai.dto.FindQuestionStatisticsQuery;
import com.dodibo.learncore.elearningcore.ai.dto.UserQuestionStatisticsResponse;
import com.dodibo.learncore.tenant.Tenant;
import com.dodibo.learncore.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionStatisticsServiceImpl implements QuestionStatisticsService {

    private final UserQuestionStatisticsRepository statisticsRepository;
    private final UserQuestionStatisticsMapper statisticsMapper;
    private final TenantMemberResolver tenantMemberResolver;

    @Override
    @Transactional(readOnly = true)
    public Page<UserQuestionStatisticsResponse> getMyStatistics(FindQuestionStatisticsQuery query) {
        Tenant tenant = tenantMemberResolver.requireCallerTenant();
        User user = tenantMemberResolver.requireCurrentUser();
        Specification<UserQuestionStatistics> spec = UserQuestionStatisticsSpecification.fromQuery(
                query, tenant.getId(), user.getId());
        return statisticsRepository.findAll(spec, query.toPageable())
                .map(statisticsMapper::toResponse);
    }
}
