package com.dodibo.learncore.elearningcore.weekly_quiz;

import org.springdoc.core.annotations.ParameterObject;
import com.dodibo.learncore.elearningcore.weekly_quiz.dto.CreateWeeklyQuizRequest;
import com.dodibo.learncore.elearningcore.weekly_quiz.dto.FindWeeklyQuizQuery;
import com.dodibo.learncore.elearningcore.weekly_quiz.dto.UpdateWeeklyQuizRequest;
import com.dodibo.learncore.elearningcore.weekly_quiz.dto.WeeklyQuizResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.dodibo.learncore.permission.PermissionCodes.TENANT_WEEKLY_QUIZ_CREATE;
import static com.dodibo.learncore.permission.PermissionCodes.TENANT_WEEKLY_QUIZ_DELETE;
import static com.dodibo.learncore.permission.PermissionCodes.TENANT_WEEKLY_QUIZ_READ;
import static com.dodibo.learncore.permission.PermissionCodes.TENANT_WEEKLY_QUIZ_UPDATE;

@RestController
@RequestMapping("weekly-quizzes")
@RequiredArgsConstructor
@Tag(name = "Weekly Quizzes")
public class WeeklyQuizController {

    private final WeeklyQuizService weeklyQuizService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authz.can('" + TENANT_WEEKLY_QUIZ_CREATE + "')")
    public ResponseEntity<WeeklyQuizResponse> createWeeklyQuiz(@RequestBody @Valid CreateWeeklyQuizRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(weeklyQuizService.createWeeklyQuiz(request));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@authz.can('" + TENANT_WEEKLY_QUIZ_READ + "')")
    public ResponseEntity<Page<WeeklyQuizResponse>> getWeeklyQuizzes(@ParameterObject FindWeeklyQuizQuery query) {
        return ResponseEntity.ok(weeklyQuizService.getWeeklyQuizzes(query));
    }

    @GetMapping("{uuid}")
    @PreAuthorize("@authz.can('" + TENANT_WEEKLY_QUIZ_READ + "')")
    public ResponseEntity<WeeklyQuizResponse> getWeeklyQuiz(@PathVariable String uuid) {
        return ResponseEntity.ok(weeklyQuizService.getWeeklyQuiz(uuid));
    }

    @PatchMapping("{uuid}")
    @PreAuthorize("@authz.can('" + TENANT_WEEKLY_QUIZ_UPDATE + "')")
    public ResponseEntity<WeeklyQuizResponse> updateWeeklyQuiz(
            @PathVariable String uuid,
            @RequestBody @Valid UpdateWeeklyQuizRequest request) {
        return ResponseEntity.ok(weeklyQuizService.updateWeeklyQuiz(uuid, request));
    }

    @DeleteMapping("{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authz.can('" + TENANT_WEEKLY_QUIZ_DELETE + "')")
    public ResponseEntity<Void> deleteWeeklyQuiz(@PathVariable String uuid) {
        weeklyQuizService.deleteWeeklyQuiz(uuid);
        return ResponseEntity.noContent().build();
    }
}
