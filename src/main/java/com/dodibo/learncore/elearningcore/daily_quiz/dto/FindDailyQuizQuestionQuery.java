package com.dodibo.learncore.elearningcore.daily_quiz.dto;

import com.dodibo.learncore.elearningcore.common.OrderedAssignmentQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindDailyQuizQuestionQuery extends OrderedAssignmentQuery {

    private String dailyQuizUuid;
    private String questionUuid;
}
