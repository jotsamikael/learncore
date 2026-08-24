package com.dodibo.learncore.elearningcore.weekly_quiz;

import org.springdoc.core.annotations.ParameterObject;
import com.dodibo.learncore.elearningcore.weekly_quiz.dto.AssignWeeklyQuizQuestionRequest;
import com.dodibo.learncore.elearningcore.weekly_quiz.dto.FindWeeklyQuizQuestionQuery;
import com.dodibo.learncore.elearningcore.weekly_quiz.dto.UpdateWeeklyQuizQuestionRequest;
import com.dodibo.learncore.elearningcore.weekly_quiz.dto.WeeklyQuizQuestionResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.dodibo.learncore.permission.PermissionCodes.TENANT_WEEKLY_QUIZ_READ;
import static com.dodibo.learncore.permission.PermissionCodes.TENANT_WEEKLY_QUIZ_UPDATE;

@RestController
@RequestMapping("weekly-quiz-questions")
@RequiredArgsConstructor
@Tag(name = "Weekly Quiz Questions")
public class WeeklyQuizQuestionController {

    private final WeeklyQuizQuestionService weeklyQuizQuestionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authz.can('" + TENANT_WEEKLY_QUIZ_UPDATE + "')")
    public ResponseEntity<WeeklyQuizQuestionResponse> assignQuestion(
            @RequestBody @Valid AssignWeeklyQuizQuestionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(weeklyQuizQuestionService.assignQuestion(request));
    }

    @GetMapping
    @PreAuthorize("@authz.can('" + TENANT_WEEKLY_QUIZ_READ + "')")
    public ResponseEntity<Page<WeeklyQuizQuestionResponse>> getWeeklyQuizQuestions(
            @ParameterObject FindWeeklyQuizQuestionQuery query) {
        return ResponseEntity.ok(weeklyQuizQuestionService.getWeeklyQuizQuestions(query));
    }

    @PatchMapping("{id}")
    @PreAuthorize("@authz.can('" + TENANT_WEEKLY_QUIZ_UPDATE + "')")
    public ResponseEntity<WeeklyQuizQuestionResponse> updateAssignment(
            @PathVariable Long id,
            @RequestBody @Valid UpdateWeeklyQuizQuestionRequest request) {
        return ResponseEntity.ok(weeklyQuizQuestionService.updateAssignment(id, request));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authz.can('" + TENANT_WEEKLY_QUIZ_UPDATE + "')")
    public ResponseEntity<Void> removeAssignment(@PathVariable Long id) {
        weeklyQuizQuestionService.removeAssignment(id);
        return ResponseEntity.noContent().build();
    }
}
