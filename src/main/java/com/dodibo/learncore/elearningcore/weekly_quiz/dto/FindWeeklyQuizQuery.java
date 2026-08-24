package com.dodibo.learncore.elearningcore.weekly_quiz.dto;

import com.dodibo.learncore.common.dto.PaginationQuery;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FindWeeklyQuizQuery extends PaginationQuery {

    private LocalDate weekStart;
}
