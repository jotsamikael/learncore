package com.dodibo.learncore.elearningcore.session.dto;

import com.dodibo.learncore.elearningcore.session.enums.SessionType;
import jakarta.validation.constraints.NotNull;

public record StartQuizSessionRequest(
        @NotNull
        SessionType sessionType,

        String categoryUuid,

        String dailyQuizUuid,

        String weeklyQuizUuid
) {
}
