package com.dodibo.learncore.elearningcore.daily_quiz;

import com.dodibo.learncore.elearningcore.daily_quiz.dto.CreateDailyQuizRequest;
import com.dodibo.learncore.elearningcore.daily_quiz.dto.DailyQuizResponse;
import com.dodibo.learncore.tenant.Tenant;
import org.springframework.stereotype.Component;

@Component
public class DailyQuizMapper {

    public DailyQuizResponse toResponse(DailyQuiz dailyQuiz) {
        if (dailyQuiz == null) {
            return null;
        }
        return new DailyQuizResponse(dailyQuiz.getUuid(), dailyQuiz.getQuizDate());
    }

    public DailyQuiz toEntity(CreateDailyQuizRequest request, Tenant tenant) {
        if (request == null) {
            return null;
        }
        return DailyQuiz.builder()
                .tenant(tenant)
                .quizDate(request.quizDate())
                .build();
    }
}
