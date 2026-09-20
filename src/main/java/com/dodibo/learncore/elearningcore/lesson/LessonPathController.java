package com.dodibo.learncore.elearningcore.lesson;

import com.dodibo.learncore.elearningcore.lesson.dto.LessonPathResponse;
import com.dodibo.learncore.elearningcore.lesson.dto.StudentLessonDetailResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.dodibo.learncore.permission.PermissionCodes.TENANT_LESSON_PATH_READ;
import static com.dodibo.learncore.permission.PermissionCodes.TENANT_LESSON_PROGRESS_UPDATE;

@RestController
@RequestMapping("lesson-path")
@RequiredArgsConstructor
@Tag(name = "Lesson Path")
public class LessonPathController {

    private final LessonPathService lessonPathService;

    @GetMapping
    @PreAuthorize("@authz.can('" + TENANT_LESSON_PATH_READ + "')")
    public ResponseEntity<LessonPathResponse> getCategoryPath(
            @RequestParam String categoryUuid
    ) {
        return ResponseEntity.ok(lessonPathService.getCategoryPath(categoryUuid));
    }

    @GetMapping("lessons/{uuid}")
    @PreAuthorize("@authz.can('" + TENANT_LESSON_PATH_READ + "')")
    public ResponseEntity<StudentLessonDetailResponse> getLesson(@PathVariable String uuid) {
        return ResponseEntity.ok(lessonPathService.getLessonForStudent(uuid));
    }

    @PostMapping("lessons/{uuid}/complete")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@authz.can('" + TENANT_LESSON_PROGRESS_UPDATE + "')")
    public ResponseEntity<StudentLessonDetailResponse> completeLesson(@PathVariable String uuid) {
        return ResponseEntity.ok(lessonPathService.completeLesson(uuid));
    }
}
