package com.dodibo.learncore.elearningcore.weekly_quiz;

import com.dodibo.learncore.elearningcore.weekly_quiz.dto.CreateWeeklyQuizRequest;
import com.dodibo.learncore.elearningcore.weekly_quiz.dto.WeeklyQuizResponse;
import com.dodibo.learncore.tenant.Tenant;
import org.springframework.stereotype.Component;

@Component
public class WeeklyQuizMapper {

    public WeeklyQuizResponse toResponse(WeeklyQuiz weeklyQuiz) {
        if (weeklyQuiz == null) {
            return null;
        }
        return new WeeklyQuizResponse(weeklyQuiz.getUuid(), weeklyQuiz.getWeekStart());
    }

    public WeeklyQuiz toEntity(CreateWeeklyQuizRequest request, Tenant tenant) {
        if (request == null) {
            return null;
        }
        return WeeklyQuiz.builder()
                .tenant(tenant)
                .weekStart(request.weekStart())
                .build();
    }
}
