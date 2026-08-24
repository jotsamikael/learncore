package com.dodibo.learncore.elearningcore.bookmark.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateBookmarkRequest(
        @NotBlank
        String questionUuid
) {
}
