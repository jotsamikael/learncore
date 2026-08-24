package com.dodibo.learncore.elearningcore.question;

import com.dodibo.learncore.elearningcore.category.Category;
import com.dodibo.learncore.elearningcore.question.dto.CreateQuestionDto;
import com.dodibo.learncore.elearningcore.question.dto.GetQuestionResponse;
import com.dodibo.learncore.elearningcore.question.dto.UpdateQuestionDto;
import org.springframework.stereotype.Component;

@Component
public class QuestionMapper {

    public GetQuestionResponse toResponse(Question question) {
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
                category != null ? category.getName() : null
        );
    }

    public Question toEntity(CreateQuestionDto request, Long tenantId, Category category) {
        if (request == null) {
            return null;
        }
        return Question.builder()
                .tenantId(tenantId)
                .category(category)
                .difficultyLevel(request.difficultyLevel())
                .questionType(request.questionType())
                .questionText(request.questionText())
                .explanation(request.explanation())
                .imageUrl(request.imageUrl())
                .build();
    }

    public void applyUpdate(Question question, UpdateQuestionDto request, Category category) {
        question.setDifficultyLevel(request.difficultyLevel());
        question.setQuestionType(request.questionType());
        question.setQuestionText(request.questionText());
        question.setExplanation(request.explanation());
        question.setImageUrl(request.imageUrl());
        question.setCategory(category);
    }
}
