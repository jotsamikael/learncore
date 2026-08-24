package com.dodibo.learncore.elearningcore.ai;

import com.dodibo.learncore.elearningcore.ai.dto.FindQuestionStatisticsQuery;
import com.dodibo.learncore.elearningcore.ai.dto.UserQuestionStatisticsResponse;
import org.springframework.data.domain.Page;

public interface QuestionStatisticsService {

    Page<UserQuestionStatisticsResponse> getMyStatistics(FindQuestionStatisticsQuery query);
}
