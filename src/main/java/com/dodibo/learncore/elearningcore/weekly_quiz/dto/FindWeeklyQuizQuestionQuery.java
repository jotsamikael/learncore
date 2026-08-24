package com.dodibo.learncore.elearningcore.weekly_quiz.dto;

import com.dodibo.learncore.common.dto.PaginationQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindWeeklyQuizQuestionQuery extends PaginationQuery {

    private String weeklyQuizUuid;
    private String questionUuid;
}
