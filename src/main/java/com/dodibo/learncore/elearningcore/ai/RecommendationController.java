package com.dodibo.learncore.elearningcore.ai;

import org.springdoc.core.annotations.ParameterObject;
import com.dodibo.learncore.elearningcore.ai.dto.FindQuestionStatisticsQuery;
import com.dodibo.learncore.elearningcore.ai.dto.UserQuestionStatisticsResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.dodibo.learncore.permission.PermissionCodes.TENANT_QUESTION_STATISTICS_READ;

@RestController
@RequestMapping("question-statistics")
@RequiredArgsConstructor
@Tag(name = "Question Statistics")
public class RecommendationController {

    private final QuestionStatisticsService questionStatisticsService;

    @GetMapping
    @PreAuthorize("@authz.can('" + TENANT_QUESTION_STATISTICS_READ + "')")
    public ResponseEntity<Page<UserQuestionStatisticsResponse>> getMyStatistics(
            @ParameterObject FindQuestionStatisticsQuery query) {
        return ResponseEntity.ok(questionStatisticsService.getMyStatistics(query));
    }
}
