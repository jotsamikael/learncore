package com.dodibo.learncore.elearningcore.question.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class GradingCriterionRequest {

    @NotBlank(message = "Criterion title is required")
    private String title;

    @NotBlank(message = "Criterion description is required")
    private String description;

    @NotNull(message = "Criterion score is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Criterion score must be greater than 0")
    private BigDecimal score;

    private Set<String> keywords = new HashSet<>();
}
