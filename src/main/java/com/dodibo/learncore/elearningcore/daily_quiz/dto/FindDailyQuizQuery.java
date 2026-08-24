package com.dodibo.learncore.elearningcore.daily_quiz.dto;

import com.dodibo.learncore.common.dto.PaginationQuery;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FindDailyQuizQuery extends PaginationQuery {

    private LocalDate quizDate;
}
