package com.dodibo.learncore.elearningcore.language;

import org.springdoc.core.annotations.ParameterObject;
import com.dodibo.learncore.elearningcore.language.dto.CreateLanguageRequest;
import com.dodibo.learncore.elearningcore.language.dto.FindLanguageQuery;
import com.dodibo.learncore.elearningcore.language.dto.LanguageResponse;
import com.dodibo.learncore.elearningcore.language.dto.UpdateLanguageRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.dodibo.learncore.permission.PermissionCodes.TENANT_LANGUAGE_CREATE;
import static com.dodibo.learncore.permission.PermissionCodes.TENANT_LANGUAGE_DELETE;
import static com.dodibo.learncore.permission.PermissionCodes.TENANT_LANGUAGE_READ;
import static com.dodibo.learncore.permission.PermissionCodes.TENANT_LANGUAGE_UPDATE;

@RestController
@RequestMapping("languages")
@RequiredArgsConstructor
@Tag(name = "languages")
public class LanguageController {

    private final LanguageService languageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authz.can('" + TENANT_LANGUAGE_CREATE + "')")
    public ResponseEntity<LanguageResponse> createLanguage(@RequestBody @Valid CreateLanguageRequest createLanguageRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(languageService.createLanguage(createLanguageRequest));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@authz.can('" + TENANT_LANGUAGE_READ + "')")
    public ResponseEntity<Page<LanguageResponse>> getLanguages(@ParameterObject FindLanguageQuery query) {
        return ResponseEntity.ok(languageService.getLanguages(query));
    }

    @GetMapping("{uuid}")
    @PreAuthorize("@authz.can('" + TENANT_LANGUAGE_READ + "')")
    public ResponseEntity<LanguageResponse> getLanguage(@PathVariable String uuid) {
        return ResponseEntity.ok(languageService.getLanguage(uuid));
    }

    @PatchMapping("{uuid}")
    @PreAuthorize("@authz.can('" + TENANT_LANGUAGE_UPDATE + "')")
    public ResponseEntity<LanguageResponse> updateLanguage(
            @PathVariable String uuid,
            @RequestBody @Valid UpdateLanguageRequest request) {
        return ResponseEntity.ok(languageService.updateLanguage(uuid, request));
    }

    @DeleteMapping("{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authz.can('" + TENANT_LANGUAGE_DELETE + "')")
    public ResponseEntity<Void> deleteLanguage(@PathVariable String uuid) {
        languageService.deleteLanguage(uuid);
        return ResponseEntity.noContent().build();
    }
}
