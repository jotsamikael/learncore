package com.dodibo.learncore.elearningcore.bookmark;

import com.dodibo.learncore.elearningcore.bookmark.dto.BookmarkResponse;
import com.dodibo.learncore.elearningcore.question.Question;
import com.dodibo.learncore.tenant.Tenant;
import com.dodibo.learncore.user.User;
import org.springframework.stereotype.Component;

@Component
public class BookmarkMapper {

    public BookmarkResponse toResponse(Bookmark bookmark) {
        if (bookmark == null) {
            return null;
        }
        Question question = bookmark.getQuestion();
        return new BookmarkResponse(
                bookmark.getUuid(),
                question != null ? question.getUuid() : null,
                question != null ? question.getQuestionText() : null
        );
    }

    public Bookmark toEntity(Tenant tenant, User user, Question question) {
        return Bookmark.builder()
                .tenant(tenant)
                .user(user)
                .question(question)
                .build();
    }
}
