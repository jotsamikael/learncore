package com.dodibo.learncore.elearningcore.lesson;

import com.dodibo.learncore.elearningcore.lesson.dto.CreateLessonRequest;
import com.dodibo.learncore.elearningcore.lesson.dto.FindLessonQuery;
import com.dodibo.learncore.elearningcore.lesson.dto.LessonResponse;
import com.dodibo.learncore.elearningcore.lesson.dto.UpdateLessonRequest;
import org.springframework.data.domain.Page;

public interface LessonService {

    LessonResponse createLesson(CreateLessonRequest request);

    Page<LessonResponse> getLessons(FindLessonQuery query);

    LessonResponse getLesson(String uuid);

    LessonResponse updateLesson(String uuid, UpdateLessonRequest request);

    void deleteLesson(String uuid);
}
