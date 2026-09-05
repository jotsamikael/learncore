package com.dodibo.learncore.elearningcore.weekly_quiz.dto;

import com.dodibo.learncore.elearningcore.common.OrderedAssignmentQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindWeeklyQuizQuestionQuery extends OrderedAssignmentQuery {

    private String weeklyQuizUuid;
    private String questionUuid;
}
