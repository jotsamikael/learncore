package com.dodibo.learncore.elearningcore.session;

import org.springdoc.core.annotations.ParameterObject;
import com.dodibo.learncore.elearningcore.session.dto.FindQuizSessionQuery;
import com.dodibo.learncore.elearningcore.session.dto.QuizSessionResponse;
import com.dodibo.learncore.elearningcore.session.dto.StartQuizSessionRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.dodibo.learncore.permission.PermissionCodes.TENANT_SESSION_CREATE;
import static com.dodibo.learncore.permission.PermissionCodes.TENANT_SESSION_READ;

@RestController
@RequestMapping("quiz-sessions")
@RequiredArgsConstructor
@Tag(name = "Quiz Sessions")
public class QuizSessionController {

    private final QuizSessionService quizSessionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authz.can('" + TENANT_SESSION_CREATE + "')")
    public ResponseEntity<QuizSessionResponse> startSession(@RequestBody @Valid StartQuizSessionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quizSessionService.startSession(request));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@authz.can('" + TENANT_SESSION_READ + "')")
    public ResponseEntity<Page<QuizSessionResponse>> getSessions(@ParameterObject FindQuizSessionQuery query) {
        return ResponseEntity.ok(quizSessionService.getSessions(query));
    }
}
