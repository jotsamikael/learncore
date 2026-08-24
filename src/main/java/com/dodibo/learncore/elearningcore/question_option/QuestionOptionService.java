package com.dodibo.learncore.elearningcore.question_option;

import com.dodibo.learncore.elearningcore.question_option.dto.CreateQuestionOptionRequest;
import com.dodibo.learncore.elearningcore.question_option.dto.FindQuestionOptionQuery;
import com.dodibo.learncore.elearningcore.question_option.dto.QuestionOptionResponseDto;
import com.dodibo.learncore.elearningcore.question_option.dto.UpdateQuestionOptionRequest;
import org.springframework.data.domain.Page;

public interface QuestionOptionService {

    QuestionOptionResponseDto createQuestionOption(CreateQuestionOptionRequest request);

    Page<QuestionOptionResponseDto> getQuestionOptions(FindQuestionOptionQuery query);

    QuestionOptionResponseDto getQuestionOption(String uuid);

    QuestionOptionResponseDto updateQuestionOption(String uuid, UpdateQuestionOptionRequest request);

    void deleteQuestionOption(String uuid);
}
