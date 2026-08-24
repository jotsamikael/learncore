package com.dodibo.learncore.elearningcore.daily_quiz;

import com.dodibo.learncore.elearningcore.daily_quiz.dto.CreateDailyQuizRequest;
import com.dodibo.learncore.elearningcore.daily_quiz.dto.DailyQuizResponse;
import com.dodibo.learncore.elearningcore.daily_quiz.dto.FindDailyQuizQuery;
import com.dodibo.learncore.elearningcore.daily_quiz.dto.UpdateDailyQuizRequest;
import org.springframework.data.domain.Page;

public interface DailyQuizService {

    DailyQuizResponse createDailyQuiz(CreateDailyQuizRequest request);

    Page<DailyQuizResponse> getDailyQuizzes(FindDailyQuizQuery query);

    DailyQuizResponse getDailyQuiz(String uuid);

    DailyQuizResponse updateDailyQuiz(String uuid, UpdateDailyQuizRequest request);

    void deleteDailyQuiz(String uuid);
}
