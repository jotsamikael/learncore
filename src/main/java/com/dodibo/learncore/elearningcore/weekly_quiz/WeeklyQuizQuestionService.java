package com.dodibo.learncore.elearningcore.weekly_quiz;

import com.dodibo.learncore.elearningcore.weekly_quiz.dto.AssignWeeklyQuizQuestionRequest;
import com.dodibo.learncore.elearningcore.weekly_quiz.dto.FindWeeklyQuizQuestionQuery;
import com.dodibo.learncore.elearningcore.weekly_quiz.dto.UpdateWeeklyQuizQuestionRequest;
import com.dodibo.learncore.elearningcore.weekly_quiz.dto.WeeklyQuizQuestionResponse;
import org.springframework.data.domain.Page;

public interface WeeklyQuizQuestionService {

    WeeklyQuizQuestionResponse assignQuestion(AssignWeeklyQuizQuestionRequest request);

    Page<WeeklyQuizQuestionResponse> getWeeklyQuizQuestions(FindWeeklyQuizQuestionQuery query);

    WeeklyQuizQuestionResponse updateAssignment(Long id, UpdateWeeklyQuizQuestionRequest request);

    void removeAssignment(Long id);
}
