package com.dodibo.learncore.elearningcore.question_option;

import org.springdoc.core.annotations.ParameterObject;
import com.dodibo.learncore.elearningcore.question_option.dto.CreateQuestionOptionForm;
import com.dodibo.learncore.elearningcore.question_option.dto.CreateQuestionOptionRequest;
import com.dodibo.learncore.elearningcore.question_option.dto.FindQuestionOptionQuery;
import com.dodibo.learncore.elearningcore.question_option.dto.QuestionOptionResponseDto;
import com.dodibo.learncore.elearningcore.question_option.dto.UpdateQuestionOptionForm;
import com.dodibo.learncore.elearningcore.question_option.dto.UpdateQuestionOptionRequest;
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
@RequestMapping("question-options")
@RequiredArgsConstructor
@Tag(name = "Question Options")
public class QuestionOptionController {

    private final QuestionOptionService questionOptionService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authz.can('" + TENANT_QUESTION_CREATE + "')")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
            schema = @Schema(implementation = CreateQuestionOptionForm.class)
    ))
    public ResponseEntity<QuestionOptionResponseDto> createQuestionOption(
            @Valid @ModelAttribute CreateQuestionOptionRequest request,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(questionOptionService.createQuestionOption(request, image));
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

    @PatchMapping(value = "{uuid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@authz.can('" + TENANT_QUESTION_UPDATE + "')")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
            schema = @Schema(implementation = UpdateQuestionOptionForm.class)
    ))
    public ResponseEntity<QuestionOptionResponseDto> updateQuestionOption(
            @PathVariable String uuid,
            @Valid @ModelAttribute UpdateQuestionOptionRequest request,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(questionOptionService.updateQuestionOption(uuid, request, image));
    }

    @DeleteMapping("{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authz.can('" + TENANT_QUESTION_DELETE + "')")
    public ResponseEntity<Void> deleteQuestionOption(@PathVariable String uuid) {
        questionOptionService.deleteQuestionOption(uuid);
        return ResponseEntity.noContent().build();
    }
}
