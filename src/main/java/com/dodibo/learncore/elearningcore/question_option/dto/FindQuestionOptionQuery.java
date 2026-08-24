package com.dodibo.learncore.elearningcore.question_option.dto;

import com.dodibo.learncore.common.dto.PaginationQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindQuestionOptionQuery extends PaginationQuery {
    private String questionUuid;
}
