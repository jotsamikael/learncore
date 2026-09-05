package com.dodibo.learncore.elearningcore.question;

import com.dodibo.learncore.elearningcore.question.dto.GradingCriterionRequest;
import com.dodibo.learncore.elearningcore.question.dto.GradingCriterionResponse;
import com.dodibo.learncore.elearningcore.question.dto.WrittenAnswerConfigRequest;
import com.dodibo.learncore.elearningcore.question.dto.WrittenAnswerConfigResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Component
public class WrittenAnswerConfigMapper {

    public WrittenAnswerConfig toEntity(WrittenAnswerConfigRequest request, Question question) {
        if (request == null) {
            return null;
        }
        WrittenAnswerConfig config = WrittenAnswerConfig.builder()
                .question(question)
                .referenceAnswer(request.getReferenceAnswer())
                .maxScore(request.getMaxScore())
                .gradingStrategy(request.getGradingStrategy())
                .minimumScoreThreshold(request.getMinimumScoreThreshold())
                .gradingCriteria(new ArrayList<>())
                .build();
        applyCriteria(request, config);
        return config;
    }

    public void applyUpdate(WrittenAnswerConfig config, WrittenAnswerConfigRequest request) {
        config.setReferenceAnswer(request.getReferenceAnswer());
        config.setMaxScore(request.getMaxScore());
        config.setGradingStrategy(request.getGradingStrategy());
        config.setMinimumScoreThreshold(request.getMinimumScoreThreshold());
        config.getGradingCriteria().clear();
        applyCriteria(request, config);
    }

    public WrittenAnswerConfigResponse toResponse(WrittenAnswerConfig config) {
        if (config == null) {
            return null;
        }
        List<GradingCriterionResponse> criteria = config.getGradingCriteria() == null
                ? List.of()
                : config.getGradingCriteria().stream().map(this::toCriterionResponse).toList();
        return new WrittenAnswerConfigResponse(
                config.getUuid(),
                config.getReferenceAnswer(),
                config.getMaxScore(),
                config.getGradingStrategy(),
                config.getMinimumScoreThreshold(),
                criteria
        );
    }

    private void applyCriteria(WrittenAnswerConfigRequest request, WrittenAnswerConfig config) {
        if (request.getGradingCriteria() == null) {
            return;
        }
        for (GradingCriterionRequest criterionRequest : request.getGradingCriteria()) {
            config.getGradingCriteria().add(toCriterionEntity(criterionRequest, config));
        }
    }

    private GradingCriterion toCriterionEntity(GradingCriterionRequest request, WrittenAnswerConfig config) {
        return GradingCriterion.builder()
                .writtenAnswerConfig(config)
                .title(request.getTitle())
                .description(request.getDescription())
                .score(request.getScore())
                .keywords(request.getKeywords() == null ? new HashSet<>() : new HashSet<>(request.getKeywords()))
                .build();
    }

    private GradingCriterionResponse toCriterionResponse(GradingCriterion criterion) {
        return new GradingCriterionResponse(
                criterion.getUuid(),
                criterion.getTitle(),
                criterion.getDescription(),
                criterion.getScore(),
                criterion.getKeywords() == null ? new HashSet<>() : new HashSet<>(criterion.getKeywords())
        );
    }
}
