package com.dodibo.learncore.elearningcore.language;

import com.dodibo.learncore.elearningcore.language.dto.CreateLanguageRequest;
import com.dodibo.learncore.elearningcore.language.dto.FindLanguageQuery;
import com.dodibo.learncore.elearningcore.language.dto.LanguageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.dodibo.learncore.permission.PermissionCodes.TENANT_LANGUAGE_CREATE;
import static com.dodibo.learncore.permission.PermissionCodes.TENANT_LANGUAGE_READ;

@RestController
@RequestMapping("languages")
@RequiredArgsConstructor
@Tag(name="languages")
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
    public ResponseEntity<Page<LanguageResponse>> getLanguages(@ModelAttribute FindLanguageQuery query){
        return ResponseEntity.status(HttpStatus.OK).body(languageService.getLanguages(query));
    }
}
