package com.dodibo.learncore.elearningcore.daily_quiz;

import org.springdoc.core.annotations.ParameterObject;
import com.dodibo.learncore.elearningcore.daily_quiz.dto.AssignDailyQuizQuestionRequest;
import com.dodibo.learncore.elearningcore.daily_quiz.dto.DailyQuizQuestionResponse;
import com.dodibo.learncore.elearningcore.daily_quiz.dto.FindDailyQuizQuestionQuery;
import com.dodibo.learncore.elearningcore.daily_quiz.dto.UpdateDailyQuizQuestionRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.dodibo.learncore.permission.PermissionCodes.TENANT_DAILY_QUIZ_READ;
import static com.dodibo.learncore.permission.PermissionCodes.TENANT_DAILY_QUIZ_UPDATE;

@RestController
@RequestMapping("daily-quiz-questions")
@RequiredArgsConstructor
@Tag(name = "Daily Quiz Questions")
public class DailyQuizQuestionController {

    private final DailyQuizQuestionService dailyQuizQuestionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authz.can('" + TENANT_DAILY_QUIZ_UPDATE + "')")
    public ResponseEntity<DailyQuizQuestionResponse> assignQuestion(
            @RequestBody @Valid AssignDailyQuizQuestionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dailyQuizQuestionService.assignQuestion(request));
    }

    @GetMapping
    @PreAuthorize("@authz.can('" + TENANT_DAILY_QUIZ_READ + "')")
    public ResponseEntity<Page<DailyQuizQuestionResponse>> getDailyQuizQuestions(
            @ParameterObject FindDailyQuizQuestionQuery query) {
        return ResponseEntity.ok(dailyQuizQuestionService.getDailyQuizQuestions(query));
    }

    @PatchMapping("{id}")
    @PreAuthorize("@authz.can('" + TENANT_DAILY_QUIZ_UPDATE + "')")
    public ResponseEntity<DailyQuizQuestionResponse> updateAssignment(
            @PathVariable Long id,
            @RequestBody @Valid UpdateDailyQuizQuestionRequest request) {
        return ResponseEntity.ok(dailyQuizQuestionService.updateAssignment(id, request));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authz.can('" + TENANT_DAILY_QUIZ_UPDATE + "')")
    public ResponseEntity<Void> removeAssignment(@PathVariable Long id) {
        dailyQuizQuestionService.removeAssignment(id);
        return ResponseEntity.noContent().build();
    }
}
