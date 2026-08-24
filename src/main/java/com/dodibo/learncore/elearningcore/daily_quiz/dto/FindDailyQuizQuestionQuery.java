package com.dodibo.learncore.elearningcore.daily_quiz.dto;

import com.dodibo.learncore.common.dto.PaginationQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindDailyQuizQuestionQuery extends PaginationQuery {

    private String dailyQuizUuid;
    private String questionUuid;
}
