package com.dodibo.learncore.elearningcore.weekly_quiz;

import com.dodibo.learncore.elearningcore.weekly_quiz.dto.CreateWeeklyQuizRequest;
import com.dodibo.learncore.elearningcore.weekly_quiz.dto.FindWeeklyQuizQuery;
import com.dodibo.learncore.elearningcore.weekly_quiz.dto.UpdateWeeklyQuizRequest;
import com.dodibo.learncore.elearningcore.weekly_quiz.dto.WeeklyQuizResponse;
import org.springframework.data.domain.Page;

public interface WeeklyQuizService {

    WeeklyQuizResponse createWeeklyQuiz(CreateWeeklyQuizRequest request);

    Page<WeeklyQuizResponse> getWeeklyQuizzes(FindWeeklyQuizQuery query);

    WeeklyQuizResponse getWeeklyQuiz(String uuid);

    WeeklyQuizResponse updateWeeklyQuiz(String uuid, UpdateWeeklyQuizRequest request);

    void deleteWeeklyQuiz(String uuid);
}
