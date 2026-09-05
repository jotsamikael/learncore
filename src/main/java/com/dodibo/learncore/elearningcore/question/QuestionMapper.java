package com.dodibo.learncore.elearningcore.question;

import com.dodibo.learncore.elearningcore.category.Category;
import com.dodibo.learncore.elearningcore.question.dto.CreateQuestionDto;
import com.dodibo.learncore.elearningcore.question.dto.GetQuestionResponse;
import com.dodibo.learncore.elearningcore.question.dto.UpdateQuestionDto;
import com.dodibo.learncore.elearningcore.question.dto.WrittenAnswerConfigRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuestionMapper {

    private final WrittenAnswerConfigMapper writtenAnswerConfigMapper;

    public GetQuestionResponse toResponse(Question question) {
        return toResponse(question, true);
    }

    public GetQuestionResponse toResponse(Question question, boolean includeWrittenConfig) {
        if (question == null) {
            return null;
        }
        Category category = question.getCategory();
        return new GetQuestionResponse(
                question.getUuid(),
                question.getDifficultyLevel(),
                question.getQuestionType(),
                question.getQuestionText(),
                question.getExplanation(),
                question.getImageUrl(),
                category != null ? category.getUuid() : null,
                category != null ? category.getName() : null,
                includeWrittenConfig
                        ? writtenAnswerConfigMapper.toResponse(question.getWrittenAnswerConfig())
                        : null
        );
    }

    public Question toEntity(CreateQuestionDto request, Long tenantId, Category category, String imageUrl) {
        if (request == null) {
            return null;
        }
        Question question = Question.builder()
                .tenantId(tenantId)
                .category(category)
                .difficultyLevel(request.difficultyLevel())
                .questionType(request.questionType())
                .questionText(request.questionText())
                .explanation(request.explanation())
                .imageUrl(imageUrl)
                .build();
        attachWrittenAnswerConfig(question, request.writtenAnswerConfig());
        return question;
    }

    public void applyUpdate(Question question, UpdateQuestionDto request, Category category) {
        question.setDifficultyLevel(request.difficultyLevel());
        question.setQuestionType(request.questionType());
        question.setQuestionText(request.questionText());
        question.setExplanation(request.explanation());
        question.setCategory(category);
        applyWrittenAnswerConfigUpdate(question, request.writtenAnswerConfig());
    }

    private void attachWrittenAnswerConfig(Question question, WrittenAnswerConfigRequest configRequest) {
        if (configRequest == null) {
            return;
        }
        question.setWrittenAnswerConfig(writtenAnswerConfigMapper.toEntity(configRequest, question));
    }

    private void applyWrittenAnswerConfigUpdate(Question question, WrittenAnswerConfigRequest configRequest) {
        if (configRequest == null) {
            question.setWrittenAnswerConfig(null);
            return;
        }
        WrittenAnswerConfig existing = question.getWrittenAnswerConfig();
        if (existing == null) {
            question.setWrittenAnswerConfig(writtenAnswerConfigMapper.toEntity(configRequest, question));
            return;
        }
        writtenAnswerConfigMapper.applyUpdate(existing, configRequest);
    }
}
