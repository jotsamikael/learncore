package com.dodibo.learncore.elearningcore.question;

import com.dodibo.learncore.elearningcore.question.dto.CreateQuestionDto;
import com.dodibo.learncore.elearningcore.question.dto.CreateQuestionForm;
import com.dodibo.learncore.elearningcore.question.dto.UpdateQuestionDto;
import com.dodibo.learncore.elearningcore.question.dto.UpdateQuestionForm;
import com.dodibo.learncore.elearningcore.question.dto.WrittenAnswerConfigRequest;
import com.dodibo.learncore.exception.OperationNotPermittedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class QuestionFormMapper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final Validator validator;

    public CreateQuestionDto toCreateDto(CreateQuestionForm form) {
        return new CreateQuestionDto(
                form.getDifficultyLevel(),
                form.getQuestionType(),
                form.getQuestionText(),
                form.getExplanation(),
                form.getCategoryUuid(),
                parseWrittenAnswerConfig(form.getWrittenAnswerConfig())
        );
    }

    public UpdateQuestionDto toUpdateDto(UpdateQuestionForm form) {
        return new UpdateQuestionDto(
                form.getDifficultyLevel(),
                form.getQuestionType(),
                form.getQuestionText(),
                form.getExplanation(),
                form.getCategoryUuid(),
                parseWrittenAnswerConfig(form.getWrittenAnswerConfig())
        );
    }

    private WrittenAnswerConfigRequest parseWrittenAnswerConfig(String json) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            WrittenAnswerConfigRequest config = OBJECT_MAPPER.readValue(json, WrittenAnswerConfigRequest.class);
            validate(config);
            return config;
        } catch (JsonProcessingException exception) {
            throw new OperationNotPermittedException("Invalid writtenAnswerConfig JSON: " + exception.getOriginalMessage());
        }
    }

    private void validate(WrittenAnswerConfigRequest config) {
        Set<ConstraintViolation<WrittenAnswerConfigRequest>> violations = validator.validate(config);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new OperationNotPermittedException(message);
        }
    }
}
