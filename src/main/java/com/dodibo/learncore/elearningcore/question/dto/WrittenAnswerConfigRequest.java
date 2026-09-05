package com.dodibo.learncore.elearningcore.question.dto;

import com.dodibo.learncore.elearningcore.question.enums.GradingStrategy;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class WrittenAnswerConfigRequest {

    @NotBlank
    private String referenceAnswer;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "maxScore must be greater than 0")
    private BigDecimal maxScore;

    @NotNull
    private GradingStrategy gradingStrategy;

    private Double minimumScoreThreshold;

    @Valid
    private List<GradingCriterionRequest> gradingCriteria = new ArrayList<>();
}
