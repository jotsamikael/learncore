package com.dodibo.learncore.elearningcore.lesson.dto;

import com.dodibo.learncore.common.dto.PaginationQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindLessonQuery extends PaginationQuery {

    private String title;
    private String categoryUuid;
    private Boolean isPremium;
}
