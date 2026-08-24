package com.dodibo.learncore.elearningcore.lesson;

import org.springdoc.core.annotations.ParameterObject;
import com.dodibo.learncore.elearningcore.lesson.dto.CreateLessonRequest;
import com.dodibo.learncore.elearningcore.lesson.dto.FindLessonQuery;
import com.dodibo.learncore.elearningcore.lesson.dto.LessonResponse;
import com.dodibo.learncore.elearningcore.lesson.dto.UpdateLessonRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.dodibo.learncore.permission.PermissionCodes.TENANT_LESSON_CREATE;
import static com.dodibo.learncore.permission.PermissionCodes.TENANT_LESSON_DELETE;
import static com.dodibo.learncore.permission.PermissionCodes.TENANT_LESSON_READ;
import static com.dodibo.learncore.permission.PermissionCodes.TENANT_LESSON_UPDATE;

@RestController
@RequestMapping("lessons")
@RequiredArgsConstructor
@Tag(name = "Lessons")
public class LessonController {

    private final LessonService lessonService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authz.can('" + TENANT_LESSON_CREATE + "')")
    public ResponseEntity<LessonResponse> createLesson(@RequestBody @Valid CreateLessonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.createLesson(request));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@authz.can('" + TENANT_LESSON_READ + "')")
    public ResponseEntity<Page<LessonResponse>> getLessons(@ParameterObject FindLessonQuery query) {
        return ResponseEntity.ok(lessonService.getLessons(query));
    }

    @GetMapping("{uuid}")
    @PreAuthorize("@authz.can('" + TENANT_LESSON_READ + "')")
    public ResponseEntity<LessonResponse> getLesson(@PathVariable String uuid) {
        return ResponseEntity.ok(lessonService.getLesson(uuid));
    }

    @PatchMapping("{uuid}")
    @PreAuthorize("@authz.can('" + TENANT_LESSON_UPDATE + "')")
    public ResponseEntity<LessonResponse> updateLesson(
            @PathVariable String uuid,
            @RequestBody @Valid UpdateLessonRequest request) {
        return ResponseEntity.ok(lessonService.updateLesson(uuid, request));
    }

    @DeleteMapping("{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authz.can('" + TENANT_LESSON_DELETE + "')")
    public ResponseEntity<Void> deleteLesson(@PathVariable String uuid) {
        lessonService.deleteLesson(uuid);
        return ResponseEntity.noContent().build();
    }
}
