package com.dodibo.learncore.elearningcore.session;

import com.dodibo.learncore.elearningcore.session.dto.FindQuizSessionQuery;
import com.dodibo.learncore.elearningcore.session.dto.QuizSessionResponse;
import com.dodibo.learncore.elearningcore.session.dto.StartQuizSessionRequest;
import org.springframework.data.domain.Page;

public interface QuizSessionService {

    QuizSessionResponse startSession(StartQuizSessionRequest request);

    Page<QuizSessionResponse> getSessions(FindQuizSessionQuery query);
}
