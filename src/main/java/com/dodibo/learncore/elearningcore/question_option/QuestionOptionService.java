package com.dodibo.learncore.elearningcore.question_option;

import com.dodibo.learncore.elearningcore.question_option.dto.CreateQuestionOptionRequest;
import com.dodibo.learncore.elearningcore.question_option.dto.FindQuestionOptionQuery;
import com.dodibo.learncore.elearningcore.question_option.dto.QuestionOptionResponseDto;
import com.dodibo.learncore.elearningcore.question_option.dto.UpdateQuestionOptionRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface QuestionOptionService {

    QuestionOptionResponseDto createQuestionOption(CreateQuestionOptionRequest request, MultipartFile image);

    Page<QuestionOptionResponseDto> getQuestionOptions(FindQuestionOptionQuery query);

    QuestionOptionResponseDto getQuestionOption(String uuid);

    QuestionOptionResponseDto updateQuestionOption(String uuid, UpdateQuestionOptionRequest request, MultipartFile image);

    void deleteQuestionOption(String uuid);
}
