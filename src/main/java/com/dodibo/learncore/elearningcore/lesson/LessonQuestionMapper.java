package com.dodibo.learncore.elearningcore.lesson;

import com.dodibo.learncore.elearningcore.lesson.dto.LessonQuestionResponse;
import com.dodibo.learncore.elearningcore.question.Question;
import org.springframework.stereotype.Component;

@Component
public class LessonQuestionMapper {

    public LessonQuestionResponse toResponse(LessonQuestion assignment) {
        if (assignment == null) {
            return null;
        }
        Lesson lesson = assignment.getLesson();
        Question question = assignment.getQuestion();
        return new LessonQuestionResponse(
                assignment.getId(),
                lesson != null ? lesson.getUuid() : null,
                question != null ? question.getUuid() : null,
                question != null ? question.getQuestionText() : null,
                assignment.getDisplayOrder()
        );
    }
}
