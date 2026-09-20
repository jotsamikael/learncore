package com.dodibo.learncore.elearningcore.lesson;

import com.dodibo.learncore.elearningcore.lesson.dto.LessonPathResponse;
import com.dodibo.learncore.elearningcore.lesson.dto.StudentLessonDetailResponse;

public interface LessonPathService {

    LessonPathResponse getCategoryPath(String categoryUuid);

    StudentLessonDetailResponse getLessonForStudent(String lessonUuid);

    StudentLessonDetailResponse completeLesson(String lessonUuid);
}
