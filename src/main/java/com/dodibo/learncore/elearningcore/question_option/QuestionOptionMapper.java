package com.dodibo.learncore.elearningcore.question_option;

import com.dodibo.learncore.elearningcore.question.Question;
import com.dodibo.learncore.elearningcore.question_option.dto.CreateQuestionOptionRequest;
import com.dodibo.learncore.elearningcore.question_option.dto.QuestionOptionResponseDto;
import com.dodibo.learncore.elearningcore.question_option.dto.UpdateQuestionOptionRequest;
import org.springframework.stereotype.Component;

@Component
public class QuestionOptionMapper {

    public QuestionOptionResponseDto toResponse(QuestionOption option) {
        if (option == null) {
            return null;
        }
        Question question = option.getQuestion();
        return new QuestionOptionResponseDto(
                option.getUuid(),
                option.getOptionText(),
                option.isCorrect(),
                option.getImageUrl(),
                question != null ? question.getUuid() : null
        );
    }

    public QuestionOption toEntity(CreateQuestionOptionRequest request, Question question) {
        if (request == null) {
            return null;
        }
        return QuestionOption.builder()
                .question(question)
                .optionText(request.optionText())
                .correct(request.correct())
                .imageUrl(request.imageUrl())
                .build();
    }

    public void applyUpdate(QuestionOption option, UpdateQuestionOptionRequest request) {
        option.setOptionText(request.optionText());
        option.setCorrect(request.correct());
        option.setImageUrl(request.imageUrl());
    }
}
