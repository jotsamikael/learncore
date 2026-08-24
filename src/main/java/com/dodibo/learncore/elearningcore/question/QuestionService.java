package com.dodibo.learncore.elearningcore.question;

import com.dodibo.learncore.elearningcore.question.dto.CreateQuestionDto;
import com.dodibo.learncore.elearningcore.question.dto.FindQuestionQuery;
import com.dodibo.learncore.elearningcore.question.dto.GetQuestionResponse;
import com.dodibo.learncore.elearningcore.question.dto.UpdateQuestionDto;
import org.springframework.data.domain.Page;

public interface QuestionService {

    GetQuestionResponse createQuestion(CreateQuestionDto request);

    Page<GetQuestionResponse> getQuestions(FindQuestionQuery query);

    GetQuestionResponse getQuestion(String uuid);

    GetQuestionResponse updateQuestion(String uuid, UpdateQuestionDto request);

    void deleteQuestion(String uuid);
}
