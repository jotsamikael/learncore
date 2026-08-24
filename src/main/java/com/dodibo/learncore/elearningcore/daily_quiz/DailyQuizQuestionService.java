package com.dodibo.learncore.elearningcore.daily_quiz;

import com.dodibo.learncore.elearningcore.daily_quiz.dto.AssignDailyQuizQuestionRequest;
import com.dodibo.learncore.elearningcore.daily_quiz.dto.DailyQuizQuestionResponse;
import com.dodibo.learncore.elearningcore.daily_quiz.dto.FindDailyQuizQuestionQuery;
import com.dodibo.learncore.elearningcore.daily_quiz.dto.UpdateDailyQuizQuestionRequest;
import org.springframework.data.domain.Page;

public interface DailyQuizQuestionService {

    DailyQuizQuestionResponse assignQuestion(AssignDailyQuizQuestionRequest request);

    Page<DailyQuizQuestionResponse> getDailyQuizQuestions(FindDailyQuizQuestionQuery query);

    DailyQuizQuestionResponse updateAssignment(Long id, UpdateDailyQuizQuestionRequest request);

    void removeAssignment(Long id);
}
