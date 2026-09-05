package com.dodibo.learncore.elearningcore.question.dto;

import com.dodibo.learncore.elearningcore.question.enums.DifficultyLevel;
import com.dodibo.learncore.elearningcore.question.enums.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateQuestionForm {

    @NotNull
    private DifficultyLevel difficultyLevel;

    @NotNull
    private QuestionType questionType;

    @NotNull
    @NotBlank
    private String questionText;

    private String explanation;

    @NotNull
    @NotBlank
    private String categoryUuid;

    @Schema(
            description = "Optional JSON string for STRUCTURAL/ESSAY questions",
            example = """
                    {
                      "referenceAnswer": "Paris",
                      "maxScore": 1.0,
                      "gradingStrategy": "EXACT_MATCH",
                      "minimumScoreThreshold": 0.9
                    }
                    """
    )
    private String writtenAnswerConfig;
}
