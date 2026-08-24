package com.dodibo.learncore.elearningcore.session;

import com.dodibo.learncore.elearningcore.category.Category;
import com.dodibo.learncore.elearningcore.daily_quiz.DailyQuiz;
import com.dodibo.learncore.elearningcore.session.dto.QuizSessionResponse;
import com.dodibo.learncore.elearningcore.session.dto.StartQuizSessionRequest;
import com.dodibo.learncore.elearningcore.weekly_quiz.WeeklyQuiz;
import com.dodibo.learncore.tenant.Tenant;
import com.dodibo.learncore.user.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class QuizSessionMapper {

    public QuizSessionResponse toResponse(QuizSession session) {
        if (session == null) {
            return null;
        }
        Category category = session.getCategory();
        DailyQuiz dailyQuiz = session.getDailyQuiz();
        WeeklyQuiz weeklyQuiz = session.getWeeklyQuiz();
        return new QuizSessionResponse(
                session.getUuid(),
                session.getSessionType(),
                category != null ? category.getUuid() : null,
                dailyQuiz != null ? dailyQuiz.getUuid() : null,
                weeklyQuiz != null ? weeklyQuiz.getUuid() : null,
                session.getStartedAt(),
                session.getCompletedAt(),
                session.getTotalQuestions(),
                session.getCorrectAnswers(),
                session.getScorePercentage(),
                session.getXpEarned(),
                session.getDurationSeconds()
        );
    }

    public QuizSession toEntity(
            StartQuizSessionRequest request,
            Tenant tenant,
            User user,
            Category category,
            DailyQuiz dailyQuiz,
            WeeklyQuiz weeklyQuiz
    ) {
        if (request == null) {
            return null;
        }
        return QuizSession.builder()
                .tenant(tenant)
                .user(user)
                .sessionType(request.sessionType())
                .category(category)
                .dailyQuiz(dailyQuiz)
                .weeklyQuiz(weeklyQuiz)
                .startedAt(LocalDateTime.now())
                .build();
    }
}
