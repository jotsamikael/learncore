package com.dodibo.learncore.elearningcore.session.dto;

import com.dodibo.learncore.common.dto.PaginationQuery;
import com.dodibo.learncore.elearningcore.session.enums.SessionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindQuizSessionQuery extends PaginationQuery {

    private SessionType sessionType;
    private Boolean completed;
}
