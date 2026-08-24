package com.dodibo.learncore.elearningcore.question_option;

import org.springdoc.core.annotations.ParameterObject;
import com.dodibo.learncore.elearningcore.question_option.dto.CreateQuestionOptionRequest;
import com.dodibo.learncore.elearningcore.question_option.dto.FindQuestionOptionQuery;
import com.dodibo.learncore.elearningcore.question_option.dto.QuestionOptionResponseDto;
import com.dodibo.learncore.elearningcore.question_option.dto.UpdateQuestionOptionRequest;
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
@RequestMapping("question-options")
@RequiredArgsConstructor
@Tag(name = "Question Options")
public class QuestionOptionController {

    private final QuestionOptionService questionOptionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authz.can('" + TENANT_QUESTION_CREATE + "')")
    public ResponseEntity<QuestionOptionResponseDto> createQuestionOption(
            @RequestBody @Valid CreateQuestionOptionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(questionOptionService.createQuestionOption(request));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@authz.can('" + TENANT_QUESTION_READ + "')")
    public ResponseEntity<Page<QuestionOptionResponseDto>> getQuestionOptions(
            @ParameterObject FindQuestionOptionQuery query) {
        return ResponseEntity.ok(questionOptionService.getQuestionOptions(query));
    }

    @GetMapping("{uuid}")
    @PreAuthorize("@authz.can('" + TENANT_QUESTION_READ + "')")
    public ResponseEntity<QuestionOptionResponseDto> getQuestionOption(@PathVariable String uuid) {
        return ResponseEntity.ok(questionOptionService.getQuestionOption(uuid));
    }

    @PatchMapping("{uuid}")
    @PreAuthorize("@authz.can('" + TENANT_QUESTION_UPDATE + "')")
    public ResponseEntity<QuestionOptionResponseDto> updateQuestionOption(
            @PathVariable String uuid,
            @RequestBody @Valid UpdateQuestionOptionRequest request) {
        return ResponseEntity.ok(questionOptionService.updateQuestionOption(uuid, request));
    }

    @DeleteMapping("{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authz.can('" + TENANT_QUESTION_DELETE + "')")
    public ResponseEntity<Void> deleteQuestionOption(@PathVariable String uuid) {
        questionOptionService.deleteQuestionOption(uuid);
        return ResponseEntity.noContent().build();
    }
}
