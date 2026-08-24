package com.dodibo.learncore.elearningcore.daily_quiz;

import org.springdoc.core.annotations.ParameterObject;
import com.dodibo.learncore.elearningcore.daily_quiz.dto.CreateDailyQuizRequest;
import com.dodibo.learncore.elearningcore.daily_quiz.dto.DailyQuizResponse;
import com.dodibo.learncore.elearningcore.daily_quiz.dto.FindDailyQuizQuery;
import com.dodibo.learncore.elearningcore.daily_quiz.dto.UpdateDailyQuizRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.dodibo.learncore.permission.PermissionCodes.TENANT_DAILY_QUIZ_CREATE;
import static com.dodibo.learncore.permission.PermissionCodes.TENANT_DAILY_QUIZ_DELETE;
import static com.dodibo.learncore.permission.PermissionCodes.TENANT_DAILY_QUIZ_READ;
import static com.dodibo.learncore.permission.PermissionCodes.TENANT_DAILY_QUIZ_UPDATE;

@RestController
@RequestMapping("daily-quizzes")
@RequiredArgsConstructor
@Tag(name = "Daily Quizzes")
public class DailyQuizController {

    private final DailyQuizService dailyQuizService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authz.can('" + TENANT_DAILY_QUIZ_CREATE + "')")
    public ResponseEntity<DailyQuizResponse> createDailyQuiz(@RequestBody @Valid CreateDailyQuizRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dailyQuizService.createDailyQuiz(request));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@authz.can('" + TENANT_DAILY_QUIZ_READ + "')")
    public ResponseEntity<Page<DailyQuizResponse>> getDailyQuizzes(@ParameterObject FindDailyQuizQuery query) {
        return ResponseEntity.ok(dailyQuizService.getDailyQuizzes(query));
    }

    @GetMapping("{uuid}")
    @PreAuthorize("@authz.can('" + TENANT_DAILY_QUIZ_READ + "')")
    public ResponseEntity<DailyQuizResponse> getDailyQuiz(@PathVariable String uuid) {
        return ResponseEntity.ok(dailyQuizService.getDailyQuiz(uuid));
    }

    @PatchMapping("{uuid}")
    @PreAuthorize("@authz.can('" + TENANT_DAILY_QUIZ_UPDATE + "')")
    public ResponseEntity<DailyQuizResponse> updateDailyQuiz(
            @PathVariable String uuid,
            @RequestBody @Valid UpdateDailyQuizRequest request) {
        return ResponseEntity.ok(dailyQuizService.updateDailyQuiz(uuid, request));
    }

    @DeleteMapping("{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authz.can('" + TENANT_DAILY_QUIZ_DELETE + "')")
    public ResponseEntity<Void> deleteDailyQuiz(@PathVariable String uuid) {
        dailyQuizService.deleteDailyQuiz(uuid);
        return ResponseEntity.noContent().build();
    }
}
