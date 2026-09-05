package com.dodibo.learncore.elearningcore.lesson.dto;

import com.dodibo.learncore.elearningcore.common.OrderedAssignmentQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindLessonQuestionQuery extends OrderedAssignmentQuery {

    private String lessonUuid;
    private String questionUuid;
}
