package com.dodibo.learncore.elearningcore.daily_quiz;

import com.dodibo.learncore.elearningcore.daily_quiz.dto.DailyQuizQuestionResponse;
import com.dodibo.learncore.elearningcore.question.Question;
import org.springframework.stereotype.Component;

@Component
public class DailyQuizQuestionMapper {

    public DailyQuizQuestionResponse toResponse(DailyQuizQuestion assignment) {
        if (assignment == null) {
            return null;
        }
        DailyQuiz dailyQuiz = assignment.getDailyQuiz();
        Question question = assignment.getQuestion();
        return new DailyQuizQuestionResponse(
                assignment.getId(),
                dailyQuiz != null ? dailyQuiz.getUuid() : null,
                question != null ? question.getUuid() : null,
                question != null ? question.getQuestionText() : null,
                assignment.getDisplayOrder()
        );
    }
}
