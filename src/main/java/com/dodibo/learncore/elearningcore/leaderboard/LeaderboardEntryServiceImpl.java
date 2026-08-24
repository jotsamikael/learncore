package com.dodibo.learncore.elearningcore.leaderboard;

import com.dodibo.learncore.common.tenant.TenantStaffResolver;
import com.dodibo.learncore.elearningcore.category.Category;
import com.dodibo.learncore.elearningcore.category.CategoryRepository;
import com.dodibo.learncore.elearningcore.leaderboard.dto.CreateLeaderboardEntryRequest;
import com.dodibo.learncore.elearningcore.leaderboard.dto.FindLeaderboardEntryQuery;
import com.dodibo.learncore.elearningcore.leaderboard.dto.LeaderboardEntryResponse;
import com.dodibo.learncore.elearningcore.weekly_quiz.WeeklyQuiz;
import com.dodibo.learncore.elearningcore.weekly_quiz.WeeklyQuizRepository;
import com.dodibo.learncore.exception.ResourceNotFoundException;
import com.dodibo.learncore.tenant.Tenant;
import com.dodibo.learncore.user.User;
import com.dodibo.learncore.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class LeaderboardEntryServiceImpl implements LeaderboardEntryService {

    private final LeaderboardEntryRepository leaderboardEntryRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final WeeklyQuizRepository weeklyQuizRepository;
    private final LeaderboardEntryMapper leaderboardEntryMapper;
    private final TenantStaffResolver tenantStaffResolver;

    @Override
    @Transactional
    public LeaderboardEntryResponse createLeaderboardEntry(CreateLeaderboardEntryRequest request) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        User user = userRepository.findByUuid(request.userUuid())
                .filter(candidate -> tenant.getId().equals(candidate.getTenantId()))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.userUuid()));
        Category category = resolveCategory(request.categoryUuid(), tenant.getId());
        WeeklyQuiz weeklyQuiz = resolveWeeklyQuiz(request.weeklyQuizUuid(), tenant.getId());
        LeaderboardEntry entry = leaderboardEntryMapper.toEntity(request, tenant, user, category, weeklyQuiz);
        return leaderboardEntryMapper.toResponse(leaderboardEntryRepository.save(entry));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LeaderboardEntryResponse> getLeaderboardEntries(FindLeaderboardEntryQuery query) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        Specification<LeaderboardEntry> spec = LeaderboardEntrySpecification.fromQuery(query, tenant.getId());
        return leaderboardEntryRepository.findAll(spec, query.toPageable())
                .map(leaderboardEntryMapper::toResponse);
    }

    private Category resolveCategory(String categoryUuid, Long tenantId) {
        if (!StringUtils.hasText(categoryUuid)) {
            return null;
        }
        return categoryRepository.findByUuidAndTenantId(categoryUuid, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryUuid));
    }

    private WeeklyQuiz resolveWeeklyQuiz(String weeklyQuizUuid, Long tenantId) {
        if (!StringUtils.hasText(weeklyQuizUuid)) {
            return null;
        }
        return weeklyQuizRepository.findByUuidAndTenant_Id(weeklyQuizUuid, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Weekly quiz not found: " + weeklyQuizUuid));
    }
}
