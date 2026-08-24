package com.dodibo.learncore.elearningcore.lesson;

import org.springdoc.core.annotations.ParameterObject;
import com.dodibo.learncore.elearningcore.lesson.dto.AssignLessonQuestionRequest;
import com.dodibo.learncore.elearningcore.lesson.dto.FindLessonQuestionQuery;
import com.dodibo.learncore.elearningcore.lesson.dto.LessonQuestionResponse;
import com.dodibo.learncore.elearningcore.lesson.dto.UpdateLessonQuestionRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.dodibo.learncore.permission.PermissionCodes.TENANT_LESSON_READ;
import static com.dodibo.learncore.permission.PermissionCodes.TENANT_LESSON_UPDATE;

@RestController
@RequestMapping("lesson-questions")
@RequiredArgsConstructor
@Tag(name = "Lesson Questions")
public class LessonQuestionController {

    private final LessonQuestionService lessonQuestionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authz.can('" + TENANT_LESSON_UPDATE + "')")
    public ResponseEntity<LessonQuestionResponse> assignQuestion(@RequestBody @Valid AssignLessonQuestionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonQuestionService.assignQuestion(request));
    }

    @GetMapping
    @PreAuthorize("@authz.can('" + TENANT_LESSON_READ + "')")
    public ResponseEntity<Page<LessonQuestionResponse>> getLessonQuestions(@ParameterObject FindLessonQuestionQuery query) {
        return ResponseEntity.ok(lessonQuestionService.getLessonQuestions(query));
    }

    @PatchMapping("{id}")
    @PreAuthorize("@authz.can('" + TENANT_LESSON_UPDATE + "')")
    public ResponseEntity<LessonQuestionResponse> updateAssignment(
            @PathVariable Long id,
            @RequestBody @Valid UpdateLessonQuestionRequest request) {
        return ResponseEntity.ok(lessonQuestionService.updateAssignment(id, request));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authz.can('" + TENANT_LESSON_UPDATE + "')")
    public ResponseEntity<Void> removeAssignment(@PathVariable Long id) {
        lessonQuestionService.removeAssignment(id);
        return ResponseEntity.noContent().build();
    }
}
