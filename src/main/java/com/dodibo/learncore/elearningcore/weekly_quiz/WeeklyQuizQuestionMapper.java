package com.dodibo.learncore.elearningcore.weekly_quiz;

import com.dodibo.learncore.elearningcore.question.Question;
import com.dodibo.learncore.elearningcore.weekly_quiz.dto.WeeklyQuizQuestionResponse;
import org.springframework.stereotype.Component;

@Component
public class WeeklyQuizQuestionMapper {

    public WeeklyQuizQuestionResponse toResponse(WeeklyQuizQuestion assignment) {
        if (assignment == null) {
            return null;
        }
        WeeklyQuiz weeklyQuiz = assignment.getWeeklyQuiz();
        Question question = assignment.getQuestion();
        return new WeeklyQuizQuestionResponse(
                assignment.getId(),
                weeklyQuiz != null ? weeklyQuiz.getUuid() : null,
                question != null ? question.getUuid() : null,
                question != null ? question.getQuestionText() : null,
                assignment.getDisplayOrder()
        );
    }
}
