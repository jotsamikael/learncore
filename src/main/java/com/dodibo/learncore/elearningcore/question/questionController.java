package com.dodibo.learncore.elearningcore.question;

import org.springdoc.core.annotations.ParameterObject;
import com.dodibo.learncore.elearningcore.question.dto.CreateQuestionDto;
import com.dodibo.learncore.elearningcore.question.dto.FindQuestionQuery;
import com.dodibo.learncore.elearningcore.question.dto.GetQuestionResponse;
import com.dodibo.learncore.elearningcore.question.dto.UpdateQuestionDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.dodibo.learncore.permission.PermissionCodes.TENANT_QUESTION_CREATE;
import static com.dodibo.learncore.permission.PermissionCodes.TENANT_QUESTION_DELETE;
import static com.dodibo.learncore.permission.PermissionCodes.TENANT_QUESTION_READ;
import static com.dodibo.learncore.permission.PermissionCodes.TENANT_QUESTION_UPDATE;

@RestController
@RequestMapping("questions")
@RequiredArgsConstructor
@Tag(name = "Questions")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authz.can('" + TENANT_QUESTION_CREATE + "')")
    public ResponseEntity<GetQuestionResponse> createQuestion(@RequestBody @Valid CreateQuestionDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(questionService.createQuestion(request));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@authz.can('" + TENANT_QUESTION_READ + "')")
    public ResponseEntity<Page<GetQuestionResponse>> getQuestions(@ParameterObject FindQuestionQuery query) {
        return ResponseEntity.ok(questionService.getQuestions(query));
    }

    @GetMapping("{uuid}")
    @PreAuthorize("@authz.can('" + TENANT_QUESTION_READ + "')")
    public ResponseEntity<GetQuestionResponse> getQuestion(@PathVariable String uuid) {
        return ResponseEntity.ok(questionService.getQuestion(uuid));
    }

    @PatchMapping("{uuid}")
    @PreAuthorize("@authz.can('" + TENANT_QUESTION_UPDATE + "')")
    public ResponseEntity<GetQuestionResponse> updateQuestion(
            @PathVariable String uuid,
            @RequestBody @Valid UpdateQuestionDto request) {
        return ResponseEntity.ok(questionService.updateQuestion(uuid, request));
    }

    @DeleteMapping("{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authz.can('" + TENANT_QUESTION_DELETE + "')")
    public ResponseEntity<Void> deleteQuestion(@PathVariable String uuid) {
        questionService.deleteQuestion(uuid);
        return ResponseEntity.noContent().build();
    }
}
