package com.dodibo.learncore.elearningcore.lesson.dto;

import com.dodibo.learncore.common.dto.PaginationQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindLessonQuestionQuery extends PaginationQuery {

    private String lessonUuid;
    private String questionUuid;
}
