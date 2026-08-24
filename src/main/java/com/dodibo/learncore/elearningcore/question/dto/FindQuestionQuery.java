package com.dodibo.learncore.elearningcore.question.dto;

import com.dodibo.learncore.common.dto.PaginationQuery;
import com.dodibo.learncore.elearningcore.question.enums.DifficultyLevel;
import com.dodibo.learncore.elearningcore.question.enums.QuestionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindQuestionQuery extends PaginationQuery {
    private String questionText;
    private DifficultyLevel difficultyLevel;
    private QuestionType questionType;
    private String categoryUuid;
}
