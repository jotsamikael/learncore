package com.dodibo.learncore.elearningcore.question;

import org.springdoc.core.annotations.ParameterObject;
import com.dodibo.learncore.elearningcore.question.dto.CreateQuestionForm;
import com.dodibo.learncore.elearningcore.question.dto.FindQuestionQuery;
import com.dodibo.learncore.elearningcore.question.dto.GetQuestionResponse;
import com.dodibo.learncore.elearningcore.question.dto.UpdateQuestionForm;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final QuestionFormMapper questionFormMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authz.can('" + TENANT_QUESTION_CREATE + "')")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
            schema = @Schema(implementation = CreateQuestionForm.class)
    ))
    public ResponseEntity<GetQuestionResponse> createQuestion(
            @Valid @ModelAttribute CreateQuestionForm form,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(questionService.createQuestion(questionFormMapper.toCreateDto(form), image));
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

    @PatchMapping(value = "{uuid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@authz.can('" + TENANT_QUESTION_UPDATE + "')")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
            schema = @Schema(implementation = UpdateQuestionForm.class)
    ))
    public ResponseEntity<GetQuestionResponse> updateQuestion(
            @PathVariable String uuid,
            @Valid @ModelAttribute UpdateQuestionForm form,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(questionService.updateQuestion(uuid, questionFormMapper.toUpdateDto(form), image));
    }

    @DeleteMapping("{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authz.can('" + TENANT_QUESTION_DELETE + "')")
    public ResponseEntity<Void> deleteQuestion(@PathVariable String uuid) {
        questionService.deleteQuestion(uuid);
        return ResponseEntity.noContent().build();
    }
}
