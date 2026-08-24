package com.dodibo.learncore.elearningcore.lesson;

import com.dodibo.learncore.elearningcore.lesson.dto.AssignLessonQuestionRequest;
import com.dodibo.learncore.elearningcore.lesson.dto.FindLessonQuestionQuery;
import com.dodibo.learncore.elearningcore.lesson.dto.LessonQuestionResponse;
import com.dodibo.learncore.elearningcore.lesson.dto.UpdateLessonQuestionRequest;
import org.springframework.data.domain.Page;

public interface LessonQuestionService {

    LessonQuestionResponse assignQuestion(AssignLessonQuestionRequest request);

    Page<LessonQuestionResponse> getLessonQuestions(FindLessonQuestionQuery query);

    LessonQuestionResponse updateAssignment(Long id, UpdateLessonQuestionRequest request);

    void removeAssignment(Long id);
}
