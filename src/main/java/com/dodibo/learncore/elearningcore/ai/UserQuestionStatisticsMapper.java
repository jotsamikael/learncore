package com.dodibo.learncore.elearningcore.ai;

import com.dodibo.learncore.elearningcore.ai.dto.UserQuestionStatisticsResponse;
import com.dodibo.learncore.elearningcore.question.Question;
import org.springframework.stereotype.Component;

@Component
public class UserQuestionStatisticsMapper {

    public UserQuestionStatisticsResponse toResponse(UserQuestionStatistics statistics) {
        if (statistics == null) {
            return null;
        }
        Question question = statistics.getQuestion();
        return new UserQuestionStatisticsResponse(
                statistics.getId(),
                question != null ? question.getUuid() : null,
                question != null ? question.getQuestionText() : null,
                statistics.getAttempts(),
                statistics.getCorrectAttempts(),
                statistics.getAverageTimeSec(),
                statistics.getMasteryScore(),
                statistics.getNextReviewAt(),
                statistics.getSm2Interval(),
                statistics.getSm2Easiness(),
                statistics.getSm2Repetitions(),
                statistics.getLastAttemptedAt()
        );
    }
}
