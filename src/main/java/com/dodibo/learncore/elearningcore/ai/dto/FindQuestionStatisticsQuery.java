package com.dodibo.learncore.elearningcore.ai.dto;

import com.dodibo.learncore.common.dto.PaginationQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindQuestionStatisticsQuery extends PaginationQuery {

    private String questionUuid;
    private Boolean dueForReview;
}
